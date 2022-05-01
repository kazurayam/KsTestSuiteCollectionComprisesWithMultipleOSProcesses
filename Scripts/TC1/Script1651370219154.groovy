import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import java.util.Random;
import internal.GlobalVariable;

int min = 10
int max = 20
Random random = new Random()
int delay = random.nextInt(max + min) + min

WebUI.comment(">> " + GlobalVariable.NAME + " said: I am busy now. I will be back in ${delay} seconds")
WebUI.delay(10);
WebUI.comment(">> " + GlobalVariable.NAME + " said: Hasta la vista, baby.");