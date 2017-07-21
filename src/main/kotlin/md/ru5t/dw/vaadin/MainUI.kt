package md.ru5t.dw.vaadin

import com.vaadin.annotations.Push
import com.vaadin.annotations.Theme
import com.vaadin.annotations.Title
import com.vaadin.server.VaadinRequest
import com.vaadin.server.VaadinServletRequest
import com.vaadin.ui.Button
import com.vaadin.ui.UI
import com.vaadin.ui.VerticalLayout
//import ch.frankel.kaadin.horizontalLayout
/**
 * Created by ru5t on 6/16/17.
 */

@Push
@Title("Main UI")
@Theme("valo")
class MainUI : UI() {
    lateinit var baseAddr:String
    override fun init(request: VaadinRequest?) {
        val jettyRequest = (request as VaadinServletRequest).request
        with(jettyRequest){
            baseAddr = "$scheme://$serverName:$serverPort"
        }

        val layout = VerticalLayout()
        layout.setSizeFull()
        content = layout
        val button = Button("Report!!", Button.ClickListener {
            ui.page.open("$baseAddr/reports/megaReport", "_blank")
        })
        layout.addComponent(button)
        //  Test only
        Thread{
            var iterations = 0
            while (true) {
                Thread.sleep(1000 * 30)
                access {
                    button.caption = "Report ($iterations)"
                }
                iterations++
            }
        }.start()
    }
}