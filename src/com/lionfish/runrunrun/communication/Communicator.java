package com.lionfish.runrunrun.communication;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import com.lionfish.runrunrun.thirdparty.Base64;

public class Communicator extends Thread {

	private final static String scheme = "https";
	private final static String authority = "secure.bluehost.com";
	private final static String path = "/~lionfisk/rabbit/scores.php";
	private final static String actionStart = "action=start";
	private final static String actionSubmit = "action=submit";

	private static URL url;
	private static String salt = null;
	private static int id = -1;

	public static void retrieveSalt() {
		try {
			URI uri = new URI(scheme, authority, path, actionStart, "");
			URL url = uri.toURL();
			URLConnection conn = url.openConnection();

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));

			String s = rd.readLine();
			salt = s.split(",")[1];
			id = Integer.parseInt(s.split(",")[0]);
			rd.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		// submitScore(123, "Test", "http://www.facebook.com/lollollersen",
		// (int) game.getPlayer().getScore(), (float) game.getGameTime(),
		// game.getPlayer().getCreeps());
		try {
			URLConnection conn = url.openConnection();
			conn.getInputStream();
		} catch (Exception e) {
			e.printStackTrace();
		}

		/*
		 * BufferedReader rd = new BufferedReader(new InputStreamReader(
		 * conn.getInputStream())); StringBuffer sb = new StringBuffer(); String
		 * line; while ((line = rd.readLine()) != null) { sb.append(line); }
		 * line = sb.toString(); rd.close();
		 */
	}

	public static void submitScore(int fbUid, String fbName, String fbLink,
			int score, float gametime, int creeps) {
		if (salt == null) {
			return;
		}

		String data = "a:7:{s:5:\"magic\";s:6:\"KRANZ!\";s:8:\"gametime\";d:"
				+ gametime + ";s:5:\"score\";i:" + score + ";s:6:\"creeps\";i:"
				+ creeps + ";s:4:\"fbid\";i:" + fbUid + ";s:6:\"fbname\";s:"
				+ fbName.length() + ":\"" + fbName + "\";s:6:\"fblink\";s:"
				+ fbLink.length() + ":\"" + fbLink  + "\";}";

		// TODO encrypt!

		try {
			char[] carray = data.toCharArray();
			for (int i = 0; i < carray.length; i++) {
				carray[i] = (char) (carray[i] ^ salt.charAt(i % salt.length()));
			}
			for (int i = 0; i < carray.length; i++) {
				carray[i] = (char) (carray[i] ^ salt.charAt(salt.length()
						- (i % salt.length()) - 1));
			}

			data = new String(carray);

			data = Base64.encodeBytes(data.getBytes());

			URI uri = new URI(scheme, authority, path, actionSubmit + "&id="
					+ id + "&data=" + data, "");
			url = uri.toURL();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get a connection to the servlet.
	 */
	/*
	 * private URLConnection getServletConnection() throws
	 * MalformedURLException, IOException {
	 * 
	 * URL urlServlet = new URL(URL); URLConnection con =
	 * urlServlet.openConnection();
	 * 
	 * con.setDoInput(true); con.setDoOutput(true); con.setUseCaches(false);
	 * con.setRequestProperty( "Content-Type",
	 * "application/x-java-serialized-object");
	 * 
	 * return con; }
	 */
	/**
	 * Send the inputField data to the servlet and show the result in the
	 * outputField.
	 */
	/*
	 * void sendTestPost() { try { // get input data for sending String input =
	 * "testtesttest";
	 * 
	 * // send data to the servlet URLConnection con = getServletConnection();
	 * OutputStream outstream = con.getOutputStream(); ObjectOutputStream oos =
	 * new ObjectOutputStream(outstream); oos.writeObject(input); oos.flush();
	 * oos.close();
	 * 
	 * // receive result from servlet InputStream instr = con.getInputStream();
	 * ObjectInputStream inputFromServlet = new ObjectInputStream(instr); String
	 * result = (String) inputFromServlet.readObject();
	 * inputFromServlet.close(); instr.close();
	 * 
	 * // show result System.out.println(result);
	 * 
	 * } catch (Exception ex) { ex.printStackTrace(); } }
	 */
}
