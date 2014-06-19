import edu.umass.cs.automan.adapters.MTurk._
import edu.umass.cs.automan.core.Utilities
import edu.umass.cs.automan.core.exception.OverBudgetException
import scala.concurrent._
import scala.concurrent.duration._

object SimpleSurvey extends App {
  val opts = Utilities.unsafe_optparse(args, "SimpleSurvey.jar")

  val a = MTurkAdapter { mt =>
    mt.access_key_id = opts('key)
    mt.secret_access_key = opts('secret)
    mt.sandbox_mode = opts('sandbox).toBoolean
  }

  def which_one(text: String) = a.RadioButtonDistributionQuestion { q =>
    q.num_samples = 60
    q.text = text
    q.options = List(
      a.Option('oscar, "Oscar the Grouch"),
      a.Option('kermit, "Kermit the Frog"),
      a.Option('spongebob, "Spongebob Squarepants"),
      a.Option('cookie, "Cookie Monster"),
      a.Option('count, "The Count")
    )
    q.question_timeout_multiplier = 2 // i.e., 1 minute
  }

  try {
    val future_answer = which_one("Which one of these does not belong?")
    val answer = Await.result(future_answer, Duration.Inf)
    println("answer1 is a " + answer)

  } catch {
    case OverBudgetException(e) => println("Over budget!")
  }
}