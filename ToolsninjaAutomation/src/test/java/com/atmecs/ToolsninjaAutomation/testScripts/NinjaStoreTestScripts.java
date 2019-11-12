package com.atmecs.ToolsninjaAutomation.testScripts;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.atmecs.ToolsninjaAutomation.constants.FilePath;
import com.atmecs.ToolsninjaAutomation.constants.Locators;
import com.atmecs.ToolsninjaAutomation.constants.NullCellValueException;
import com.atmecs.ToolsninjaAutomation.constants.ValidatingData;
import com.atmecs.ToolsninjaAutomation.logReports.LogReport;
import com.atmecs.ToolsninjaAutomation.pages.HomePage;
import com.atmecs.ToolsninjaAutomation.testBase.TestBase;
import com.atmecs.ToolsninjaAutomation.testflow.NinjaStorePageFlow;
import com.atmecs.ToolsninjaAutomation.utils.ExcelDataReader;

/*
*Class validates the functionality of homepage functionality
*/
public class NinjaStoreTestScripts extends TestBase {
	LogReport log = new LogReport();
	int i = 1;
	int rowNumber;
	Locators locators = new Locators();
	HomePage home;
	ValidatingData data;
	NinjaStorePageFlow HomePageFlow;
	ExcelDataReader excelData = new ExcelDataReader();

	@BeforeClass
	public void getUrl() {
		String url = baseClass.getProperty("URL");
		driver.get(url);

		driver.manage().window().maximize();
		driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
		driver.manage().timeouts().implicitlyWait(6, TimeUnit.SECONDS);
	}

	/*
	 * Test validates the homepage redirection of the toolsninja.com
	 */
	@Test(priority = 1)
	public void homePageRedirection() {
		home = new HomePage(driver);
		data = new ValidatingData();
		HomePageFlow = new NinjaStorePageFlow(driver);
		LogReport.getlogger();
		// log = extent.startTest("HomepageRedirection");
		log.info("Starting Redirection validation");
		home.isRedirectionCorrect();
		log.info("Redirection is on the correct page");
		log.info("Starting the homepage testing");
	}

	// method validate the product availability
	@Test(priority = 2)
	public void searchProductAndCheckAvailability() throws InterruptedException, NullCellValueException {
		try {
			rowNumber = excelData.getNoOfRows(FilePath.TESTDATA_FILE, "ninjaStoreProductSpecifications");
			System.out.println(rowNumber);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while (rowNumber - 1 > 0) {

			String productName = excelData.getCellData(FilePath.TESTDATA_FILE, "ninjaStoreProductSpecifications",
					"ProductName", "TD-" + (rowNumber - 1));
			String price = excelData.getCellData(FilePath.TESTDATA_FILE, "ninjaStoreProductSpecifications", "PRICE",
					"TD-" + (rowNumber - 1));
			String exTax = excelData.getCellData(FilePath.TESTDATA_FILE, "ninjaStoreProductSpecifications", "EX TAX",
					"TD-" + (rowNumber - 1));
			System.out.println(productName + price + exTax);

			HomePageFlow.searchProduct(productName);
			home.isProductAvailable(productName);

			log.info("product name is correct");
			if (productName.equalsIgnoreCase("iphone")) {
				home.isDescriptionCorrect("descriptioniphone");

			}

			if (productName.equalsIgnoreCase("macbook air")) {
				home.isDescriptionCorrect("descriptionmac");
			}

			home.isPriceCorrect(price, exTax);
			log.info("product price and taxes is correct");
			log.info("Validation done");
			HomePageFlow.AddProduct();
			rowNumber--;
		}
	}

	@Test(priority = 3)
	public void validateGrandTotal() throws NullCellValueException, IOException {

		String quantityOfIphone = excelData.getCellData(FilePath.TESTDATA_FILE, "ninjaStoreProductSpecifications",
				"QUANTITY", "TD-2");
		HomePageFlow.manipulateQuantity(quantityOfIphone, i);
		i++;
		String quantityOfMacbook = excelData.getCellData(FilePath.TESTDATA_FILE, "ninjaStoreProductSpecifications",
				"QUANTITY", "TD-1");
		HomePageFlow.manipulateQuantity(quantityOfMacbook, i);

	}

	@Test(priority = 4)
	public void checkGrandTotal() {
		home.isGrandTotalCorrect(data.getValidatingData("grandtotal1"));
	}

	@Test(priority = 5)
	public void validateGrandTotalAfterRemovingItem() {

		HomePageFlow.removeItem();
		home.isGrandTotalCorrect(data.getValidatingData("grandtotal2"));

	}

	@Test(priority = 6)
	public void validateErrorMessage() throws NullCellValueException {

		try {
			rowNumber = excelData.getNoOfRows(FilePath.TESTDATA_FILE, "wrongProductsSpecification");
			System.out.println(rowNumber);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while (rowNumber - 1 > 0) {
			String productName = excelData.getCellData(FilePath.TESTDATA_FILE, "wrongProductsSpecification",
					"PRODUCT NAME", "TD-" + (rowNumber - 1));
			String message = excelData.getCellData(FilePath.TESTDATA_FILE, "wrongProductsSpecification", "MESSAGE",
					"TD-" + (rowNumber - 1));
			try {
				HomePageFlow.searchProduct(productName);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			home.isMessageDisplayed(message);
			rowNumber--;
		}
	}
}
