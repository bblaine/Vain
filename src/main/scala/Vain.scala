import java.io._
import java.util.Date
import net.liftweb.json.JsonAST._
import net.liftweb.json.JsonParser._
import scala.collection.mutable.ListBuffer

case class Repository(has_downloads: Boolean, watchers: Int, url: String, has_wiki: Boolean, fork: Boolean, forks: Int, created_at: Option[Date], size: Int, private_repo: Option[Boolean], open_issues: Int, pushed_at: Option[Date], name: String, owner: String, has_issues: Boolean)
case class Repositories(repos: List[Repository])

object Main {
  def fork(fork: Int): String = fork match {
    case 0 => "(FORK)"
    case _ => ""
  }

  implicit val formats = net.liftweb.json.DefaultFormats

  def main(args: Array[String]) {
    val github = new Github()
    val name = args(0)
    val user = github.getUser(name)

    val login = (user \\ "login") match {
      case login: JField => login.value.values
      case _ => Nil
    }

    val followers = (user \\ "followers_count") match {
      case followers: JField => followers.value.values
      case _ => Nil
    }

    val repo_count = (user \\ "public_repo_count") match {
      case repo_count: JField => repo_count.value.values
      case _ => Nil
    }

    //TODO - Only get repos if user valid.
    val jsonRepos = github.getRepos(name, repo_count.toString.toInt)

    System.out.println(login + " - " + followers + " followers - " + repo_count + " public repositories")

    var listRepos = ListBuffer[Repository]()

    for (i <- 0 until repo_count.toString.toInt) {
      listRepos += (jsonRepos \\ "repositories")(i).extract[Repository]
    }

    val orderedrepos = listRepos.sortBy(_.watchers).reverse

    orderedrepos.foreach(repo => printf("%-25s %12d %12s %12d %12s %6s\n", repo.name, repo.watchers, " watchers", repo.forks, " forks", fork(repo.forks)))
  }
}
