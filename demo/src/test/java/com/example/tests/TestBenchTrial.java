package com.example.tests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.vaadin.testbench.By;
import com.vaadin.testbench.ScreenshotOnFailureRule;
import com.vaadin.testbench.TestBench;
import com.vaadin.testbench.TestBenchTestCase;

public class TestBenchTrial extends TestBenchTestCase {
	private String baseUrl;
	private final StringBuffer verificationErrors = new StringBuffer();

	@Rule
	public ScreenshotOnFailureRule screenshotOnFailureRule = new ScreenshotOnFailureRule(this, true);

	@Before
	public void setUp() throws Exception {
		setDriver(TestBench.createDriver(new FirefoxDriver()));
		baseUrl = "http://localhost:8080/";
	}

	@Test
	public void trial() throws Exception {
		driver.get(concatUrl(baseUrl, "/#public"));
		testBenchElement(driver.findElement(By.id("gwt-uid-5"))).click(10, 9);
		testBenchElement(driver.findElement(By.id("gwt-uid-9"))).click(9, 6);
		testBenchElement(
				driver.findElement(By
						.vaadin("ROOT::/VVerticalLayout[0]/Slot[1]/VPanel[0]/VSplitPanelHorizontal[0]/VTree[0]#n[0]/n[1]/n[1]")))
				.click(28, 10);
		testBenchElement(
				driver.findElement(By
						.vaadin("ROOT::/VVerticalLayout[0]/Slot[1]/VPanel[0]/VSplitPanelHorizontal[0]/VTree[0]#n[0]/n[0]")))
				.click(28, 8);
		testBenchElement(driver.findElement(By.id("gwt-uid-38"))).click(119, 8);
		driver.findElement(By.id("gwt-uid-38")).clear();
		driver.findElement(By.id("gwt-uid-38")).sendKeys("ds");
		driver.findElement(By.id("gwt-uid-40")).clear();
		driver.findElement(By.id("gwt-uid-40")).sendKeys("password");
		driver.findElement(
				By.vaadin("ROOT::/VVerticalLayout[0]/Slot[1]/VPanel[0]/VGridLayout[0]/VPanel[0]/VVerticalLayout[0]/Slot[5]/VButton[0]/domChild[0]/domChild[0]"))
				.click();
		driver.findElement(
				By.vaadin("ROOT::/VVerticalLayout[0]/Slot[1]/VPanel[0]/VGridLayout[0]/VPanel[0]/VVerticalLayout[0]/Slot[5]/VButton[0]/domChild[0]/domChild[0]"))
				.click();
		testBenchElement(driver.findElement(By.xpath("//body/div[3]"))).closeNotification();
		testBenchElement(driver.findElement(By.vaadin("ROOT::Root/VNotification[0]/HTML[0]/domChild[1]/domChild[1]")))
				.click(40, 16);
		testBenchElement(driver.findElement(By.xpath("//div[@id='ROOT-2521314-overlays']/div"))).closeNotification();
		testBenchElement(driver.findElement(By.id("gwt-uid-5"))).click(15, 12);
		driver.findElement(By.id("gwt-uid-5")).clear();
		driver.findElement(By.id("gwt-uid-5")).sendKeys("ds");
		testBenchElement(driver.findElement(By.id("gwt-uid-7"))).click(23, 14);
		driver.findElement(By.id("gwt-uid-7")).clear();
		driver.findElement(By.id("gwt-uid-7")).sendKeys("password");
		driver.findElement(
				By.vaadin("ROOT::/VVerticalLayout[0]/Slot[1]/VPanel[0]/VGridLayout[0]/VPanel[0]/VVerticalLayout[0]/Slot[5]/VButton[0]/domChild[0]/domChild[0]"))
				.click();
		driver.findElement(
				By.vaadin("ROOT::/VVerticalLayout[0]/Slot[1]/VPanel[0]/VGridLayout[0]/VPanel[0]/VVerticalLayout[0]/Slot[5]/VButton[0]/domChild[0]/domChild[0]"))
				.click();
		testBenchElement(driver.findElement(By.xpath("//body/div[3]"))).closeNotification();
		testBenchElement(driver.findElement(By.vaadin("ROOT::Root/VNotification[0]/HTML[0]/domChild[1]/domChild[1]")))
				.click(55, 13);
		testBenchElement(driver.findElement(By.xpath("//div[@id='ROOT-2521314-overlays']/div"))).closeNotification();
	}

	@After
	public void tearDown() throws Exception {
		String verificationErrorString = verificationErrors.toString();
		if (!"".equals(verificationErrorString)) {
			fail(verificationErrorString);
		}
	}
}
