package org.isdb.pimonitoring.utils;

import java.time.LocalDate;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Utils {

	private static String piUserName;

	@Value("${pimonitoring.piUserName}")
	public void setPiUserName(String name) {
		piUserName = name;
	}

	private static String piPass;

	@Value("${pimonitoring.piPass}")
	public void setPiPass(String pass) {
		piPass = pass;
	}

	public static String getToday() {
		return LocalDate.now() + "T03:00:00.000+03:00";
	}

	public static String getYesterday() {
		return LocalDate.now().minusDays(1) + "T03:00:00.000+03:00";
	}

	public static String getTomorrow() {
		return LocalDate.now().plusDays(1) + "T03:00:00.000+03:00";
	}

	public static final String getBasicAuthenticationHeader() {
		String valueToEncode = piUserName + ":" + piPass;
		return "Basic " + Base64.getEncoder().encodeToString(valueToEncode.getBytes());
	}

	public static final String getUserName() {
		return piUserName;
	}

	public static final String getPassword() {
		return piPass;
	}
	
	public static void sleepForTenSeconds() {
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e1) {
		}
	}
}
