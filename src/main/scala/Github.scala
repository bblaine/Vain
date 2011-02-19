import java.io._
import net.liftweb.json.JsonAST._
import net.liftweb.json.JsonParser._
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.{ DefaultHttpClient }
import scala.collection.mutable.ListBuffer

class Github() {
  implicit val formats = net.liftweb.json.DefaultFormats

  val client = new DefaultHttpClient()

  def getUser(user: String): JValue = {

    val method = new HttpGet("http://github.com/api/v2/json/user/show/" + user)

    //TODO - Check response
    val response = client.execute(method)

    val instream = response.getEntity.getContent();
    val reader = new BufferedReader(new InputStreamReader(instream));
    val json = parse(reader.readLine())

    json \\ "user"
  }

  //TODO - Remove repoCount param so you can iterate while repos < 30
  def getRepos(user: String, repoCount: Int): JValue = {

    val pages = repoCount / 30 + 1
    var repos = ListBuffer[JValue]()

    for (i <- 1 until pages + 1) {

      val method = new HttpGet("http://github.com/api/v2/json/repos/show/" + user + "?page=" + i)

      //TODO - Check response
      val response = client.execute(method)

      val instream = response.getEntity.getContent();
      val reader = new BufferedReader(new InputStreamReader(instream))
      val line = reader.readLine

      repos += parse(line)

    }

    repos.foldLeft(JNothing: JValue)(_ merge _) \\ "repositories"

  }
}
