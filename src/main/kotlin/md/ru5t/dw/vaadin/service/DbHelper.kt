package md.ru5t.dw.vaadin.service

import com.vaadin.external.org.slf4j.Logger
import com.vaadin.external.org.slf4j.LoggerFactory
import org.hibernate.Session
import org.hibernate.SessionFactory

/**
 * Created by ru5t on 7/24/17.
 */
object DbHelper {
    lateinit var sessionFactory: SessionFactory
    val logger:Logger = LoggerFactory.getLogger(DbHelper::class.java)
    var statsList :List<Any> = listOf()

    fun <T> withSession(body: (session: Session) -> T, transactional:Boolean = false):T {
        val session = sessionFactory.openSession()
        var retData:T? = null
        try{
            if(transactional) {
                session.beginTransaction()
            }
            retData = body(session)
            if(transactional){
                val txn = session.transaction
                if (txn != null && txn.status.canRollback()) {
                    txn.commit()
                }
            }
            return retData
        }catch (e:Throwable){
            logger.error(e.message, e)
            //  rollback transaction
            if(transactional){
                val txn = session.transaction
                if (txn != null && txn.status.canRollback()) {
                    txn.rollback()
                }
            }
        }finally {
            //close session
            session.close()
        }
        return retData!!
    }

    fun queryList(session: Session, query:String, vararg params:Any):MutableList<Any>{
        val nq = session.createNativeQuery(query)
        params?.forEachIndexed { index, param ->
            //  does not support List params. they can be set only by name. But it doesn't required yet
            nq.setParameter(index+1, param)  // looks like params indexes start from 1
        }
        return nq.list() as MutableList<Any>
    }

    init {
        Thread{
            Thread.currentThread().name = "STATS_POLLING_THREAD"
            while (true){

                statsList = withSession({
                    // session is passed as it param
                    val query =
"""SELECT
    org.id,
    org.name
    FROM organization org
    WHERE org.tags like ?
"""
                    queryList(it, query, "%sell%").toList() // call with param
                })
                //  find a way to send vaadin broadcast for all ui's to refresh their's grids
                statsList?.let {
                    if(it.size >0){
                        //broadcast here
                    }
                }
                Thread.sleep(1000*30)
            }
        }.start()
    }
}