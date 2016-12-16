package cl.citiaps.twitter.streaming;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.TwitterObjectFactory;

import com.mongodb.*;
import com.mongodb.util.JSON;
import java.net.UnknownHostException;
import java.util.Date;

public class Main {
	
    public final static String OAUTH_CONSUMER_KEY = "STZ6pAqJqsiPMiN1acVlg24Xy";
    public final static String OAUTH_CONSUMER_SECRET = "4veMKR2yKe76vyGpT45RUyEdtWYAhqNK25rlDeQog6xRI9dqg0";
    public final static String OAUTH_ACCESS_TOKEN = "3184806281-TLfa8Qw56kzx3yzAYlJt13WCk3vs8jCWWPKpus0";
    public final static String OAUTH_ACCESS_TOKEN_SECRET = "nO3egtA8GPKmfX2iY8Lo3iW7omemKRZayOSBuMjwJ604b";
	
    public static void main(String[] args) {
    	new Main().doMain(args);      
    }
    
    public void doMain(String[] args){
    	ConfigurationBuilder cb = new ConfigurationBuilder();
    	cb.setDebugEnabled(true);
    	cb.setOAuthConsumerKey(OAUTH_CONSUMER_KEY);
    	cb.setOAuthConsumerSecret(OAUTH_CONSUMER_SECRET);
    	cb.setOAuthAccessToken(OAUTH_ACCESS_TOKEN);
    	cb.setOAuthAccessTokenSecret(OAUTH_ACCESS_TOKEN_SECRET);
    	cb.setJSONStoreEnabled(true);
    	

    	
    	try{
	    	Mongo mongodb = new Mongo("localhost");
	        DB baseDatos = mongodb.getDB("pruebaFiltro");
	        final DBCollection coleccion = baseDatos.getCollection("tweets4");

	    	TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
	    	StatusListener listener = new StatusListener() {  		

		        public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
		            System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
		        }

		        public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
		            System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
		        }

		        public void onScrubGeo(long userId, long upToStatusId) {
		            System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
		        }

		        public void onException(Exception ex) {
		            ex.printStackTrace();
		        }

				@Override
				public void onStallWarning(StallWarning arg0) {
					
				}

				@Override
				public void onStatus(Status status) {
					System.out.println(status.getText());
					String tweet = (String)TwitterObjectFactory.getRawJSON(status);
					DBObject documento = (DBObject)JSON.parse(tweet);
            		documento.put("created_at", new Date((String)documento.get("created_at")));
	                coleccion.insert(documento);
				}
	    	};

	    FilterQuery fq = new FilterQuery();
	    String keywords[] = {
	    "Movistar reclamo,Movistar señal,Movistar 3g,Movistar 4g,Movistar cobertura,AyudaMovistarCL",
	    "Claro reclamo,Claro señal,Claro 3g,Claro 4g,Claro cobertura,miclaro_cl",
	    "Entel reclamo,Entel señal,Entel 3g,Entel 4g,Entel cobertura,entel_ayuda",
	    "WOM reclamo,WOM señal,WOM 3g,WOM 4g,WOM cobertura,WOMteAyuda",
		"VTR reclamo,VTR señal,VTR 3g,VTR 4g,VTR cobertura,VTRSoporte"};


	    fq.track(keywords);

	    twitterStream.addListener(listener);
	    twitterStream.filter(fq);      
    		
    	}
    	catch(UnknownHostException e) {
            e.printStackTrace();
        }

    	

    	
    }



}