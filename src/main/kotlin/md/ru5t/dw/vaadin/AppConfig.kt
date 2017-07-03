package md.ru5t.dw.vaadin

import io.dropwizard.Configuration
import com.fasterxml.jackson.annotation.JsonProperty
import io.dropwizard.db.DataSourceFactory
import org.hibernate.SessionFactory
import javax.validation.Valid
import javax.validation.constraints.NotNull


/**
 * Created by ru5t on 12/7/16.
 */
object AppConfig : Configuration(){
    lateinit var dbUrl: String
    lateinit var reportDir: String
    lateinit public var sessionFactory:SessionFactory

    @Valid
    @NotNull
    private var database = DataSourceFactory()

    @JsonProperty("database")
    fun getDataSourceFactory(): DataSourceFactory {
        return database
    }

    fun setDatabase(database: DataSourceFactory) {
        this.database = database
    }
}
