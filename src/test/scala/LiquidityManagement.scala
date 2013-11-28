import org.scalatest.{ BeforeAndAfterAll, FlatSpec }
import org.scalatest.concurrent._
import org.scalatest.matchers.ShouldMatchers
import akka.actor.{ Actor, Props, ActorSystem }
import akka.testkit.{ ImplicitSender, TestKit, TestActorRef }
import scala.concurrent.duration._

class LiquidityManagementSpec(_system: ActorSystem)
  extends TestKit(_system)
  with ImplicitSender
  with ShouldMatchers
  with FlatSpec
  with BeforeAndAfterAll {

  def this() = this(ActorSystem("LiquidityManagementSpec"))

  override def afterAll: Unit = {
    system.shutdown()
    system.awaitTermination(10.seconds)
  }

  "An TieredPricer" should "be holding a price" in {
    val actor = TestActorRef(Props[TieredPricer])
    actor ! CorePrice("a", 1.2)
    actor.underlyingActor.asInstanceOf[TieredPricer].prices("a") should be(1.2)
  }

  it should "be able able to provide a price given a request" in {
    val actor = TestActorRef(Props[TieredPricer])
    actor ! CorePrice("a", 1.2)
    actor ! RFQ("a")
    expectMsgType[TieredPrice].price should be(1.2)
  }
}
