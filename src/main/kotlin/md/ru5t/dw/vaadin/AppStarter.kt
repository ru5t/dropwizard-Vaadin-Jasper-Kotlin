package md.ru5t.dw.vaadin

import com.vaadin.annotations.VaadinServletConfiguration
import com.vaadin.server.VaadinServlet
import io.dropwizard.Application
import io.dropwizard.Bundle
import io.dropwizard.assets.AssetsBundle
import io.dropwizard.db.DataSourceFactory
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import md.ru5t.dw.vaadin.resources.ReportingResource
import org.eclipse.jetty.server.session.SessionHandler
import javax.servlet.Servlet
import io.dropwizard.hibernate.HibernateBundle
import javax.persistence.*
import javax.servlet.annotation.WebInitParam
import javax.servlet.annotation.WebServlet


/**
 * Created by ru5t on 12/7/16.
 */
open class AppStarter :Application<AppConfig>(){

    override fun getName() = "Ru5t's test"

    @Entity
    @Table(name="storage")
    class Storage(
            @Id
            @GeneratedValue(strategy = GenerationType.IDENTITY)
            var id:Long,

            var name:String
    )

    private val hibernate = object : HibernateBundle <AppConfig>(Storage::class.java) {
        override fun getDataSourceFactory(configuration: AppConfig): DataSourceFactory {
            return configuration.getDataSourceFactory()
        }
    }

    override fun run(configuration: AppConfig?, environment: Environment?) {
        configuration?.sessionFactory = hibernate.sessionFactory
        configuration?.let {
            val reportingResource = ReportingResource()
            environment?.let {
                it.jersey().register(reportingResource)
            }
        }
    }

    override fun initialize(bootstrap: Bootstrap<AppConfig>?) {
        super.initialize(bootstrap)
        bootstrap?.addBundle(VaadinBundle(VServlet::class.java, "/ui/*"))
        bootstrap?.addBundle(hibernate)
    }

    //  Vaadin Servlet Wrapper                      @WebInitParam(name = "name", value = "admin")
    //@WebServlet(asyncSupported = true, initParams = Array<WebInitParam>(@WebInitParam(name = "name", value = "admin")))
    @WebServlet(
            asyncSupported = true,
            initParams = arrayOf(
                WebInitParam(name = "pushmode", value = "automatic")
            )
    )
    @VaadinServletConfiguration(ui = MainUI::class , productionMode = false)
    class VServlet : VaadinServlet()

    //  DW's bundle
    class  VaadinBundle <T : Servlet> (servlet: Class<T>, path:String) :Bundle {

        val servlets = mutableMapOf<String, Class<T>>()
        val sessionHandler : SessionHandler = SessionHandler()

        init {
            servlets.put(path, servlet)
        }

        override fun run(environment: Environment?) {
            environment?.let {
                it.servlets().setSessionHandler(sessionHandler)
                servlets.forEach {
                    environment.applicationContext.addServlet(it.value, it.key)
                }
            }
        }

        override fun initialize(bootstrap: Bootstrap<*>?) {
            val bundle = AssetsBundle ("/VAADIN", "/VAADIN", null, "vaadin")
            bootstrap?.addBundle(bundle)
        }
    }
}

fun main (args: Array<String>){
    AppStarter().run(*args)
}
