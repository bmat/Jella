package com.bmat.ella;

import static org.junit.Assert.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;

public class JellaTest {

	private Jella jella;

	@Before public void setUp() throws IOException {
		Properties params = new Properties();
		InputStream inputStream = this.getClass().getResourceAsStream("/ws.conf");
		assertNotNull("could not load test configuration ('ws.conf')", inputStream);
		params.load(inputStream);

		jella = new Jella(params.getProperty("host"), params.getProperty("user"), params.getProperty("password"));
	}

	@After public void tearDown() {
		// nothing to tear down
	}

	@Test public void testSearchTracks() throws Exception {
		// TEST 1 - search
		String query1 = "Beautiful day";
		String collection1 = "bmat";
		String method1 = "search";
		String artist1 = "u2";
		String album1 = "leave behind";
		TrackSearch trackSearch1 = jella.searchTracks(method1, query1, collection1, true, null, new String[]{"artist:" + artist1, "release:" + album1});
		assertTrue(trackSearch1.getTotalResultCount() > 0);
		ArrayList<Track> tracksResult1 = trackSearch1.getNextPage();
		assertTrue(tracksResult1.size() > 0);
		for(Track track : tracksResult1){
			assertTrue(track.getArtist().getName().toLowerCase().indexOf(artist1.toLowerCase()) != -1);
			assertTrue(track.getTitle().toLowerCase().indexOf(query1.toLowerCase()) != -1);
			assertTrue(track.getCollection().equalsIgnoreCase(collection1));
		}

		assertTrue(jella.searchTracks(method1, query1, collection1, true, null, new String[]{"artist:" + artist1, "release:" + album1}).getTotalResultCount() > 0);
		
		// TEST 2 - match
		String collection2 = "bmat";
		String method2 = "match";
		String track2 = "Buffalo Soldier";
		TrackSearch trackSearch2 = jella.searchTracks(method2, "", collection2, true, null, new String[]{"track:" + track2});
		assertTrue(trackSearch2.getTotalResultCount() > 0);
		ArrayList<Track> tracksResult2 = trackSearch2.getPage(1);
		assertTrue(tracksResult2.size() > 0);
		for(Track track : tracksResult2){
			assertTrue(track.getTitle().toLowerCase().indexOf(track2.toLowerCase()) != -1);
			assertTrue(track.getCollection().equalsIgnoreCase(collection2));
		}
		

		// TEST 3 - resolve
		String collection3 = "bmat";
		HashMap<String, String> query = new HashMap<String, String>();
		query.put("artist", "Red Hot Chili Peppers");
		query.put("track", "Otherside");
		TrackSearch trackSearch3 = jella.searchTracks(query, collection3, true, null, new String[]{"track:" + track2});
		assertTrue(trackSearch3.getTotalResultCount() > 0);
		ArrayList<Track> tracksResult3 = trackSearch3.getPage(-1);
		assertTrue(tracksResult3.size() > 0);
		for(Track track : tracksResult3){
			assertTrue(track.getTitle().toLowerCase().indexOf(query.get("track").toString().toLowerCase()) != -1);
			assertTrue(track.getArtist().getName().toLowerCase().indexOf(query.get("artist").toString().toLowerCase()) != -1);
			assertTrue(track.getCollection().equalsIgnoreCase(collection3));
		}		
	}
	
	
	@Test public void  testTrack(){
		Track track = new Track(jella.getEllaConnection(), "9dd6f275-81f4-4b4d-9277-41dfb1dad7a7", "bmat");
		assertTrue(track.getTitle().equalsIgnoreCase("californication"));
		assertTrue(track.getArtist().getName().equalsIgnoreCase("Red Hot Chili Peppers"));
		assertTrue(track.getArtistName().equalsIgnoreCase(track.getArtist().getName()));
		assertTrue(track.getAlbumTitle().equalsIgnoreCase("Californication"));
		assertNotNull(track.getLinks());
		
	}
	
	@Test public void testSearchArtists() throws Exception{
		// TEST 1 - search
		String query1 = "Red Hot Chili Peppers";
		String collection1 = "bmat";
		String method1 = "search";
		ArtistSearch artistSearch1 = jella.searchArtists(method1, query1, collection1, true, null);
		assertTrue(artistSearch1.getTotalResultCount() > 0);
		ArrayList<Artist> artistsResult1 = artistSearch1.getNextPage();
		assertTrue(artistsResult1.size() > 0);
		for(Artist artist : artistsResult1){
			assertNotNull(artist.getName());
			assertNotNull(artist.getId());
			assertTrue(artist.getName().toLowerCase().indexOf(query1.toLowerCase()) != -1);
		}
		
		// TEST 2 - match
		String query2 = "marley";
		String collection2 = "bmat";
		String method2 = "match";
		ArtistSearch artistSearch2 = jella.searchArtists(method2, query2, collection2, false, null);
		assertTrue(artistSearch2.getTotalResultCount() > 0);
		ArrayList<Artist> artistsResult2 = artistSearch2.getPage(1);
		assertTrue(artistsResult2.size() > 0);
		for(Artist artist : artistsResult2){
			assertTrue(artist.getName().toLowerCase().indexOf(query2.toLowerCase()) != -1);
		}
		
		// TEST 3 - resolve
		String query3 = "Beatles";
		String collection3 = "bmat";
		String method3 = "resolve";
		ArtistSearch artistSearch3 = jella.searchArtists(method3, query3, collection3, true, null);
		assertTrue(artistSearch3.getTotalResultCount() > 0);
		ArrayList<Artist> artistsResult3 = artistSearch3.getPage(-1);
		assertTrue(artistsResult3.size() > 0);
		assertTrue(artistsResult3.get(0).getName().toLowerCase().indexOf(query3.toLowerCase()) != -1);
	}
}
