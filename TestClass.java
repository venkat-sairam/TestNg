import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import pages.actions.LoginPageActions;
import stepDefinitions.BaseTest;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Hashtable;
import java.util.Properties;

public class TestClass {

    public WebDriverWait wait;
    public FileInputStream fileInputStream;
    public  final int TIMEOUT=30;
    public  final int PAGE_LOAD_TIMEOUT=100;


    BaseTest baseTest = new BaseTest();
    WebDriver driver;
    Properties properties;
    LoginPageActions loginPageActions;
    int rowCount;
    int colCount;
    Xls_Reader excelWorkbook;
    String sheetName;

ThreadLocal<WebDriver> d=new ThreadLocal<WebDriver>();

    public TestClass() throws IOException {


        excelWorkbook = new Xls_Reader(".\\TestCases.xlsx");

        sheetName = "TestData";
        rowCount = excelWorkbook.getRowCount(sheetName);

        colCount = excelWorkbook.getColumnCount(sheetName);
        System.out.println("rowcount= " + rowCount + " --- column count = " + colCount);
    }

    

    void setDriverThread(WebDriver driver)
    {
        d.set(driver);

    }
    WebDriver getDriverThread()
    {
        return d.get();
    }

   @Test(dataProvider = "getData")
   public void testParallelRun(Hashtable<String,String> data) throws InterruptedException, IOException {

      

       String browser= "chrome";

       if(browser.equalsIgnoreCase("chrome")) {

           WebDriverManager.chromedriver().setup();
           driver = new ChromeDriver();
            setDriverThread(driver);
           driver = getDriverThread();
       }
       driver.manage().deleteAllCookies();
       driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(TIMEOUT));
       wait = new WebDriverWait(driver,  Duration.ofSeconds(30));
       driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(PAGE_LOAD_TIMEOUT));


       driver.get("https://saucedemo.com/");
        driver.findElement(By.cssSelector("#user-name")).sendKeys(data.get("UserName"));
        driver.findElement(By.cssSelector("#password")).sendKeys(data.get("Password"));
        driver.findElement(By.cssSelector("#login-button")).click();


    }

    @DataProvider(parallel = true) // supplying data for a test method.
    public Object[][] getData() throws IOException
    {

        Object[][] data = new Object[rowCount-1][1];


        Hashtable<String,String> table = null;

        for (int rNum = 2; rNum <= rowCount; rNum++)
        {
            table = new Hashtable<String,String>();

            for (int cNum = 0; cNum < colCount; cNum++)
            {

                table.put(excelWorkbook.getCellData(sheetName, cNum, 1), excelWorkbook.getCellData(sheetName, cNum, rNum));
                data[rNum - 2][0] = table;
            }

        }

        return data;
    }


}
