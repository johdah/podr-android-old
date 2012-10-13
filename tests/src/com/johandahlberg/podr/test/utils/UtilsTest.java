package com.johandahlberg.podr.test.utils;

import java.util.Date;

import junit.framework.Assert;

import android.test.AndroidTestCase;

import com.johandahlberg.podr.utils.Utils;

public class UtilsTest extends AndroidTestCase {
    public void parseDate() throws Throwable {
		Assert.assertNotNull("Failed Sat Oct 13 23:54:15 CEST 2012", 
				Utils.parseDate("Sat Oct 13 23:54:15 CEST 2012"));
    }
}