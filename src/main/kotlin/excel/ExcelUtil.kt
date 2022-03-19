package excel

import enums.Day
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileOutputStream


object ExcelUtil {
    fun save(
        rows: Collection<Collection<String>>,
        rowOffSet: Int = 10,
        columnOffSet: Int = 5,
        path:String ,
        sheetName:String = "TimeTable"
    ) {


        //Create XSSF Workbook entity
        val workBook = XSSFWorkbook()

        //Create an excel sheet
        val sheet = workBook.createSheet(sheetName)


        rows.forEachIndexed { rowIndex, s ->


            //Specify a column with createRow
            val row = sheet.createRow(rowIndex + rowOffSet)

            //Fill in the value
            s.forEachIndexed { columnIndex, value ->
                //Specify a row with createCell
                //Specify the cell to enter the value
                val cell = row.createCell(columnIndex + columnOffSet)

                cell.setCellValue(value)

            }


        }


        val fileOutputStream = FileOutputStream(path)

        workBook.write(fileOutputStream)

        fileOutputStream.close()

    }


    fun addTitle(

    ) {
        //Create XSSF Workbook entity
        val workBook = XSSFWorkbook()

        //Create an excel sheet
        val sheet = workBook.createSheet("TimeTable")


        sheet.addMergedRegion(CellRangeAddress.valueOf("E5:L6"))

        val cell = sheet.createRow(4).createCell(5)

        cell.setCellValue("This is a large title of the excel file")
        cell.cellStyle = cellStyle(workBook) as XSSFCellStyle?


        val fileOutputStream = FileOutputStream("F:/Documents/TestFiles/test.xlsx")

        workBook.write(fileOutputStream)

        fileOutputStream.close()

    }

    fun cellStyle(workBook: Workbook): CellStyle {
        //Create a font instance
        val font = workBook.createFont()
        //Set font size
        font.fontHeightInPoints = 36.toShort()

        val style = workBook.createCellStyle()
        //Add font information to style
        style.setFont(font)
        //align center
        style.alignment = HorizontalAlignment.CENTER

        return style
    }


}


fun main() {

    val columns = listOf(
        "Monday",
        "Tuesday",
        "Wednesday",
        "Thursday",
        "Friday",
        "Saturday",
        "Sunday"
    )

    val row1 = listOf(
        "07:30-13:30",
        "07:30-13:30",
        "07:30-13:30",
        "07:30-13:30",
        "07:30-13:30",
        "07:30-13:30",
        "07:30-13:30",
    )

    val row2 = listOf(
        "13:30-19:30",
        "13:30-19:30",
        "13:30-19:30",
        "13:30-19:30",
        "13:30-19:30",
        "13:30-19:30",
        "13:30-19:30",
    )

    val row3 = listOf(
        "19:30-08:00",
        "19:30-08:00",
        "19:30-08:00",
        "19:30-08:00",
        "19:30-08:00",
        "19:30-08:00",
        "19:30-08:00",
    )

    val table = listOf(
        columns,
        row1,
        row2,
        row3
    )

    ExcelUtil.save(table, path = "F:/Documents/TestFiles/test.xlsx")

//    createTimeTable()
}
