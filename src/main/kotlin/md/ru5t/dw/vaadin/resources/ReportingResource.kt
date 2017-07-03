package md.ru5t.dw.vaadin.resources

import io.dropwizard.hibernate.UnitOfWork
import md.ru5t.dw.vaadin.AppConfig
import net.sf.jasperreports.engine.JRDataSource
import net.sf.jasperreports.engine.JasperExportManager
import net.sf.jasperreports.engine.JasperFillManager
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
import java.io.File
import java.sql.Connection
import java.util.*
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.StreamingOutput

/**
 * Created by ru5t on 6/22/17.
 */

@Path("/reports/")
class ReportingResource {

    fun printReport(reportName: String, params: Map<String, Any>? = null, dataSource: JRDataSource? = null): StreamingOutput {
        val reportPath = AppConfig.reportDir + File.separator + reportName
        val jp = if (dataSource != null)
        //  dataSource - based way
            JasperFillManager.fillReport(reportPath, params, dataSource)
        //  sql connection-based way. (no need to provide dataSource
        else JasperFillManager.fillReport(reportPath, params, null as Connection)

        return StreamingOutput {
            output ->
            JasperExportManager.exportReportToPdfStream(jp, output)
        }
    }

    @GET
    @Path("megaReport")
    @Produces("application/pdf")
    @UnitOfWork
    fun megaReport(): StreamingOutput {
        val rows = listOf(
                DSItem(Date(), "TR1", "Type1", "Transp 1", "T-18", "P1", 425345.0),
                DSItem(Date(), "TR2", "Type1", "Transp 1", "T-18", "P1", 123.0)
        )

        /*
        //  getting session and performing test query. Debug only test
        val session = AppConfig.sessionFactory.currentSession
        val query = session.createNativeQuery("SELECT * FROM organization")
        val list = query.list()
        */
        val ds = JRBeanCollectionDataSource(rows)
        return printReport("TransactionRegisterTable.jasper", dataSource = ds)
    }

}

data class DSItem(val date: Date,
                  val transaction: String,
                  val transportType: String,
                  val transportNum: String,
                  val tank: String,
                  val product: String,
                  val mass: Double)