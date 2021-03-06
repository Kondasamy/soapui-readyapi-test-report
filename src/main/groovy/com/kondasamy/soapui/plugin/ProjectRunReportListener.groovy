package com.kondasamy.soapui.plugin

/**
 * Created by Kondasamy Jayaraman
 * Contact: Kondasamy@outlook.com
 */
import com.eviware.soapui.SoapUI
import com.eviware.soapui.model.support.ProjectRunListenerAdapter
import com.eviware.soapui.model.testsuite.ProjectRunContext
import com.eviware.soapui.model.testsuite.ProjectRunner
import com.eviware.soapui.plugins.ListenerConfiguration

import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.Font
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFRow

@ListenerConfiguration
class ProjectRunReportListener extends ProjectRunListenerAdapter
{
    @Override
    void afterRun(ProjectRunner projectRunner, ProjectRunContext runContext)
    {
        try
        {
            //def currentUser = System.getProperty('user.name')
            //def now = new Date().format('yyyy-MM-dd HH:mm:ss')
            def today = new Date().format('yyyyMMdd')
            def projectName = projectRunner.project.name.replaceAll("[^a-zA-Z0-9.-]", "_")
            //userDir -> variable changed to project root directory to facilitate Jenkins job
            //def userDir = System.getProperty('user.home')
            def groovyUtils = new com.eviware.soapui.support.GroovyUtils(runContext)
            def userDir = groovyUtils.projectPath
            def SoapUIDir = new File(userDir,"\\SoapUI Test Report\\")
            def fileName = "$projectName - Test execution report - $today"+".xlsx"
            def file
            //Directory existence check
            if (SoapUIDir.exists())
            {
                file = new File(SoapUIDir,fileName)
                SoapUI.log "RESULT EXPORTER :: File exists -> "+file.absolutePath
            }
            else
            {
                SoapUIDir.mkdirs()
                file = new File(SoapUIDir,fileName)
                SoapUI.log "RESULT EXPORTER :: File doesn't exists; but created -> "+file.absolutePath
            }

            //Initiate XSSF Workbook
            XSSFWorkbook workBookWrite = new XSSFWorkbook()
            XSSFSheet sheetWrite = workBookWrite.createSheet("SoapUITestReport")
            sheetWrite.setColumnWidth(0,10000)
            sheetWrite.setColumnWidth(1,4000)
            sheetWrite.setColumnWidth(2,6000)
            sheetWrite.setColumnWidth(3,24000)
            SoapUI.log "RESULT EXPORTER :: Sheet created :: name -> "+workBookWrite.getSheetAt(0).sheetName

            //Header style
            def headerStyle = workBookWrite.createCellStyle()
            headerStyle.setFillForegroundColor(IndexedColors.GOLD.index)
            headerStyle.setFillPattern(CellStyle.SOLID_FOREGROUND)
            headerStyle.setBorderBottom(CellStyle.BORDER_DASH_DOT_DOT)
            headerStyle.setBottomBorderColor(IndexedColors.BLACK.index)
            headerStyle.setBorderLeft(CellStyle.BORDER_THIN)
            headerStyle.setLeftBorderColor(IndexedColors.GREEN.index)
            headerStyle.setBorderRight(CellStyle.BORDER_THIN)
            headerStyle.setRightBorderColor(IndexedColors.BLUE.index)
            headerStyle.setBorderTop(CellStyle.BORDER_THIN)
            headerStyle.setTopBorderColor(IndexedColors.BLACK.index)
            Font headerStyleFont = workBookWrite.createFont()
            headerStyleFont.setBold(true)
            headerStyleFont.setColor(IndexedColors.WHITE.index)
            headerStyleFont.setBoldweight(Font.BOLDWEIGHT_BOLD)
            headerStyle.setFont(headerStyleFont)
            headerStyle.setWrapText(true)
            headerStyle.setAlignment(CellStyle.ALIGN_CENTER)

            //Pass status style
            def passStyle = workBookWrite.createCellStyle()
            passStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.index)
            passStyle.setFillPattern(CellStyle.SOLID_FOREGROUND)
            passStyle.setBorderBottom(CellStyle.BORDER_THIN)
            passStyle.setBottomBorderColor(IndexedColors.BLACK.index)
            passStyle.setBorderLeft(CellStyle.BORDER_THIN)
            passStyle.setLeftBorderColor(IndexedColors.GREEN.index)
            passStyle.setBorderRight(CellStyle.BORDER_THIN)
            passStyle.setRightBorderColor(IndexedColors.BLUE.index)
            passStyle.setBorderTop(CellStyle.BORDER_THIN)
            passStyle.setTopBorderColor(IndexedColors.BLACK.index)
            passStyle.setWrapText(true)

            //Fail status style
            def failStyle = workBookWrite.createCellStyle()
            failStyle.setFillForegroundColor(IndexedColors.RED.index)
            failStyle.setFillPattern(CellStyle.SOLID_FOREGROUND)
            failStyle.setBorderBottom(CellStyle.BORDER_THIN)
            failStyle.setBottomBorderColor(IndexedColors.BLACK.index)
            failStyle.setBorderLeft(CellStyle.BORDER_THIN)
            failStyle.setLeftBorderColor(IndexedColors.GREEN.index)
            failStyle.setBorderRight(CellStyle.BORDER_THIN)
            failStyle.setRightBorderColor(IndexedColors.BLUE.index)
            failStyle.setBorderTop(CellStyle.BORDER_THIN)
            failStyle.setTopBorderColor(IndexedColors.BLACK.index)
            failStyle.setWrapText(true)

            //Default style
            def defaultStyle = workBookWrite.createCellStyle()
            defaultStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.index)
            defaultStyle.setFillPattern(CellStyle.SOLID_FOREGROUND)
            defaultStyle.setBorderBottom(CellStyle.BORDER_THIN)
            defaultStyle.setBottomBorderColor(IndexedColors.BLACK.index)
            defaultStyle.setBorderLeft(CellStyle.BORDER_THIN)
            defaultStyle.setLeftBorderColor(IndexedColors.GREEN.index)
            defaultStyle.setBorderRight(CellStyle.BORDER_THIN)
            defaultStyle.setRightBorderColor(IndexedColors.BLUE.index)
            defaultStyle.setBorderTop(CellStyle.BORDER_THIN)
            defaultStyle.setTopBorderColor(IndexedColors.BLACK.index)
            defaultStyle.setWrapText(true)

            //Initialize SoapUI data sets
            def testCaseName, testCaseErrorMessage, testRunStatus, testTimeStamp, testStepName
            def row = 0//, col = 0

            //Initialize the first row of the file with header
            XSSFRow rowData = sheetWrite.createRow(row)
            rowData.createCell(0).each
            {
                cell ->
                cell.setCellValue("TESTCASE NAME")
                cell.setCellStyle(headerStyle)
            }

            rowData.createCell(1).each
            {
                cell ->
                    cell.setCellValue("STATUS")
                    cell.setCellStyle(headerStyle)
            }
            rowData.createCell(2).each
            {
                cell ->
                    cell.setCellValue("TIMESTAMP")
                    cell.setCellStyle(headerStyle)
            }
            rowData.createCell(3).each
            {
                cell ->
                    cell.setCellValue("REMARKS")
                    cell.setCellStyle(headerStyle)
            }
            row++

            //Initiate project result collection
            projectRunner.results.each
                    {
                        testSuiteResult ->
                            testSuiteResult.results.each
                                    {
                                        testCaseResult ->
                                            //Collect SoapUI data
                                            testCaseName = testCaseResult.testCase.name
                                            testRunStatus = testCaseResult.status.toString()
                                            testTimeStamp = new Date(testCaseResult.startTime).format('yyyy-MM-dd HH:mm:ss')
                                            testCaseErrorMessage = ""
                                            //Debug trace
                                            SoapUI.log "RESULT EXPORTER :: $testCaseName , $testRunStatus, $testTimeStamp"
                                            /* Check if status falls under - INITIALIZED, RUNNING, CANCELED, FINISHED, FAILED, WARNING;*/
                                            if(testCaseResult.status.toString() == "FAILED")
                                            {
                                                testCaseResult.results.each
                                                        {
                                                            testStepResult ->
                                                            testStepResult.messages.each
                                                                    {
                                                                        message ->
                                                                            if(message.toString()!= "null")
                                                                            {
                                                                                testStepName = testStepResult.testStep.name
                                                                                testCaseErrorMessage +=  " $testStepName :: " + message.toString()+"\n"
                                                                            }
                                                                    }
                                                        }
                                                SoapUI.log "RESULT EXPORTER :: Error -> $testCaseErrorMessage"
                                            }
                                            else
                                                testCaseErrorMessage = "OK"
                                            //Initialize testcase data
                                            rowData = sheetWrite.createRow(row)
                                            if(testRunStatus == "FAILED")
                                            {
                                                rowData.createCell(0).each
                                                {
                                                    cell ->
                                                        cell.setCellValue(testCaseName.toString())
                                                        cell.setCellStyle(failStyle)
                                                }
                                                rowData.createCell(1).each
                                                {
                                                    cell ->
                                                        cell.setCellValue(testRunStatus.toString())
                                                        cell.setCellStyle(failStyle)
                                                }
                                                rowData.createCell(2).each
                                                {
                                                    cell ->
                                                        cell.setCellValue(testTimeStamp.toString())
                                                        cell.setCellStyle(failStyle)
                                                }
                                                rowData.createCell(3).each
                                                {
                                                    cell ->
                                                        cell.setCellValue(testCaseErrorMessage.toString())
                                                        cell.setCellStyle(failStyle)
                                                }
                                            }
                                            else if (testRunStatus == "FINISHED")
                                            {
                                                rowData.createCell(0).each
                                                {
                                                    cell ->
                                                        cell.setCellValue(testCaseName.toString())
                                                        cell.setCellStyle(passStyle)
                                                }
                                                rowData.createCell(1).each
                                                {
                                                    cell ->
                                                        cell.setCellValue("PASSED")
                                                        cell.setCellStyle(passStyle)
                                                }
                                                rowData.createCell(2).each
                                                {
                                                    cell ->
                                                        cell.setCellValue(testTimeStamp.toString())
                                                        cell.setCellStyle(passStyle)
                                                }
                                                rowData.createCell(3).each
                                                {
                                                    cell ->
                                                        cell.setCellValue(testCaseErrorMessage.toString())
                                                        cell.setCellStyle(passStyle)
                                                }
                                            }
                                            else
                                            {
                                                rowData.createCell(0).each
                                                {
                                                    cell ->
                                                        cell.setCellValue(testCaseName.toString())
                                                        cell.setCellStyle(defaultStyle)
                                                }
                                                rowData.createCell(1).each
                                                {
                                                    cell ->
                                                        cell.setCellValue(testRunStatus.toString())
                                                        cell.setCellStyle(defaultStyle)
                                                }
                                                rowData.createCell(2).each
                                                {
                                                    cell ->
                                                        cell.setCellValue(testTimeStamp.toString())
                                                        cell.setCellStyle(defaultStyle)
                                                }
                                                rowData.createCell(3).each
                                                {
                                                    cell ->
                                                        cell.setCellValue(testCaseErrorMessage.toString())
                                                        cell.setCellStyle(defaultStyle)
                                                }
                                            }

                                            row++
                                    }
                    }
            workBookWrite.write(new FileOutputStream(file))
            SoapUI.log "RESULT EXPORTER :: Success. Results exported to -> "+file.absolutePath
        }
        catch(Exception exception)
        {
            SoapUI.log.error "RESULT EXPORTER :: Exception occurred -> $exception"
        }
    }
}
/**
 * Created by Kondasamy Jayaraman
 * Contact: Kondasamy@outlook.com
 */