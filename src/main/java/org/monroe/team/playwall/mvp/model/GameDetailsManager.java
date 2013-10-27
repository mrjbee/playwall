package org.monroe.team.playwall.mvp.model;

import com.google.common.io.*;
import org.monroe.team.playwall.integration.FSManager;
import org.monroe.team.playwall.logging.Logs;
import org.monroe.team.playwall.mvp.ui.ImageLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * User: MisterJBee
 * Date: 10/27/13 Time: 3:50 PM
 * Open source: MIT Licence
 * (Do whatever you want with the source code)
 */
public class GameDetailsManager {

    private Executor backgroundExecutor = Executors.newSingleThreadExecutor();
    private List<FetchRequest> fetchRequestList;
    private List<FetchRequest> fetchedRequestList;
    private final FSManager fsManager;

    public GameDetailsManager(FSManager fsManager) {
        this.fsManager = fsManager;
    }

    public void start(List<Launcher> gameLauncher) {
        fetchRequestList = new ArrayList<FetchRequest>(gameLauncher.size());
        for (Launcher launcher : gameLauncher) {
            fetchRequestList.add(new FetchRequest(launcher));
        }
        fetchedRequestList = new ArrayList<FetchRequest>(fetchRequestList.size());
        submit(fetchItem());
    }

    private Runnable fetchItem() {
        return new Runnable(){
            @Override
            public void run() {
                try{
                   fetchItemImpl();
                }catch (NoInternetException e){
                   submit(waitForInternet());
                }
                if (!fetchRequestList.isEmpty())
                    submit(fetchItem());
            }
        };
    }

    private void fetchItemImpl() {
        double randIdxSeed = Math.random();
        int randomFetchIndex = (int) ((fetchRequestList.size()-1) * randIdxSeed);
        FetchRequest request = fetchRequestList.get(randomFetchIndex);
        if(!request.fetch()) return;
        fetchRequestList.remove(request);
        addToFetched(request);
    }

    private synchronized void addToFetched(FetchRequest request) {
        fetchedRequestList.add(request);
    }


    private Runnable waitForInternet() {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    //nothing bad
                }
            }
        };
    }

    private void submit(final Runnable runnable) {
        backgroundExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try{
                    runnable.run();
                } catch (Exception e){
                    Logs.GAME_DETAILS.e(e,"Unexpected error");
                }
            }
        });
    }

    public void stop() {

    }

    public synchronized GameDetails get(Launcher launcher) {
        FetchRequest request = findFetchedFor(launcher);
        if (request != null){
            return request.getGameDetails();
        }
        return new GameDetails(launcher.title, null, null, null);
    }

    private FetchRequest findFetchedFor(Launcher launcher) {
        for (FetchRequest request : fetchedRequestList) {
               if (request.launcher.equals(launcher)) return request;
        }

        return null;
    }

    private final class FetchRequest{

        private final String USER_AGENT = "Mozilla/5.0";

        private final Launcher launcher;
        private GameDetails gameDetails;

        private FetchRequest(Launcher launcher) {
            this.launcher = launcher;
        }

        public boolean fetch() {
            if ("".equals(launcher.dbId)){
                return false;
            }

            File gameFolder = fsManager.getGameInfoFolder(launcher.dbId);
            gameFolder.mkdirs();
            File[] gameFiles = gameFolder.listFiles();
            if (null == fetchFileExists(gameFiles,"game.fetch")){
                //folder fetched partially or not at all
                try {
                    fetchFromNet(launcher.dbId, gameFiles, gameFolder);
                } catch (Exception e){
                    Logs.GAME_DETAILS.e(e,"During fetching from NET");
                    return false;
                }
            }
            if (null != fetchFileExists(gameFiles,"game.fetch")){
                try {
                    List<String> overviewLines = Files.readLines(new File(gameFolder, "overview"), Charset.defaultCharset());
                    StringBuilder builder = new StringBuilder();
                    for (String overviewLine : overviewLines) {
                        builder.append(overviewLine+"\n");
                    }

                    File fanartFile = new File(gameFolder, "fanart");
                    gameDetails = new GameDetails(
                            launcher.title,
                            builder.toString(),
                            (fanartFile.exists())?fanartFile.getAbsolutePath():null,
                            ImageLoader.loadFromPath(new File(gameFolder,"boxart").getAbsolutePath()));
                    return true;
                } catch (IOException e) {
                    Logs.GAME_DETAILS.e(e,"During reading from FS");
                    return false;
                }
            }
            return false;
        }

        private void fetchFromNet(String dbId, File[] gameFiles, File gameFolder)  {
            final String url = "http://thegamesdb.net/api/GetGame.php?id="+dbId;
            String gameData = readFromUrl(url);
            Document doc = parseXML(gameData);
            String overviewText = doc.getElementsByTagName("Overview").item(0).getTextContent();
            write(overviewText, gameFolder, "overview");
            String baseImgUrl = doc.getElementsByTagName("baseImgUrl").item(0).getTextContent();
            if (null == fetchFileExists(gameFiles,"boxart.fetch")){
                fetchBoxart(dbId, gameFolder, doc, baseImgUrl);
            }

            if (null == fetchFileExists(gameFiles,"fanart.fetch")){
                NodeList fanartNodeList = doc.getElementsByTagName("fanart");
                if (fanartNodeList.getLength() != 0){
                    Node fanartNode = fanartNodeList.item(0);
                    String fanartUrlString = fanartNode.getChildNodes().item(0).getTextContent();
                    final URL fanartUrl = createUrl(baseImgUrl + fanartUrlString + "?id=" + dbId);
                    InputSupplier<InputStream> fanartSupplier = new ByteSource() {
                        @Override
                        public InputStream openStream() throws IOException {
                            HttpURLConnection con = (HttpURLConnection) fanartUrl.openConnection();
                            con.setRequestMethod("GET");
                            con.setRequestProperty("User-Agent", USER_AGENT);
                            return con.getInputStream();
                        }
                    };
                    try {
                        File fanartFile = new File(gameFolder,"fanart");
                        fanartFile.delete();
                        Files.copy(fanartSupplier, fanartFile);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                try {
                    Files.touch(new File(gameFolder,"fanart.fetch"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            try {
                Files.touch(new File(gameFolder, "game.fetch"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private void fetchBoxart(String dbId, File gameFolder, Document doc, String baseImgUrl) {
            //boxart side="front"
            NodeList boxartNodeList = doc.getElementsByTagName("boxart");
            if (boxartNodeList.getLength() != 0){
                Node boxartFrontNode = null;
                if (boxartNodeList.getLength() == 1){
                    boxartFrontNode = boxartNodeList.item(0);
                } else {
                    for (int i = 0; i < boxartNodeList.getLength(); i++){
                        if ("front".equals(boxartNodeList.item(i).getAttributes().getNamedItem("side").getTextContent())){
                            boxartFrontNode = boxartNodeList.item(i);
                            break;
                        }
                        if (boxartFrontNode == null) boxartFrontNode = boxartNodeList.item(0);
                    }
                }
                String boxartUrlString = boxartFrontNode.getTextContent();
                final URL boxartUrl = createUrl(baseImgUrl + boxartUrlString + "?id=" + dbId);
                InputSupplier<InputStream> boxartSupplier = new ByteSource() {
                    @Override
                    public InputStream openStream() throws IOException {
                        HttpURLConnection con = (HttpURLConnection) boxartUrl.openConnection();
                        con.setRequestMethod("GET");
                        con.setRequestProperty("User-Agent", USER_AGENT);
                        return con.getInputStream();
                    }
                };
                try {
                    File boxartFile = new File(gameFolder,"boxart");
                    boxartFile.delete();
                    Files.copy(boxartSupplier, boxartFile);
                    Files.touch(new File(gameFolder,"boxart.fetch"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        private void write(String overviewText, File gameFolder, String fileName) {
            File fileToWrite = new File(gameFolder,fileName);
            fileToWrite.delete();
            try {
                Files.write(overviewText, fileToWrite, Charset.defaultCharset());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String text = "cached";
            fileToWrite = new File(gameFolder,fileName+".fetch");
            fileToWrite.delete();
            try {
                Files.write(overviewText, fileToWrite, Charset.defaultCharset());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private Document parseXML(String gameData) {
            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
            f.setValidating(false);
            DocumentBuilder builder = null;
            Document doc= null;
            try {
                builder = f.newDocumentBuilder();
                doc = builder.parse(new InputSource(new StringReader(gameData)));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return doc;
        }

        private String readFromUrl(String url) {
            String gameData;URL gameFetchUrl = createUrl(url);
            HttpURLConnection con = null;
            int responseCode = 500;
            try {
                con = (HttpURLConnection) gameFetchUrl.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("User-Agent", USER_AGENT);
                responseCode = con.getResponseCode();
            } catch (IOException e) {
                throw new NoInternetException(e);
            }
            if (responseCode > 500) throw new NoInternetException(new Exception("Response is "+responseCode));

            BufferedReader in = null;
            try {
                in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                gameData = response.toString();
            } catch (IOException e) {
                throw new NoInternetException(e);
            }
            finally {
              if (in != null){
                  try {
                      in.close();
                  } catch (IOException e) {}
              }
            }
            return gameData;
        }

        private URL createUrl(String url) {
            URL gameFetchUrl = null;
            try {
                gameFetchUrl = new URL(url);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            return gameFetchUrl;
        }

        private File fetchFileExists(File[] gameFiles, String fileName) {
            if (gameFiles == null) return null;
            for (File gameFile : gameFiles) {
                if (gameFile.getName().equals(fileName)){
                    return gameFile;
                }
            }
            return null;
        }

        public GameDetails getGameDetails() {
            return gameDetails;
        }
    }

    private final class NoInternetException extends RuntimeException{

        public NoInternetException(Exception e) {
            super(e);
        }
    }

}
