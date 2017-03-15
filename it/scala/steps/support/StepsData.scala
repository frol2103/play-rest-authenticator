package steps.support
import cucumber.runtime.java.guice.ScenarioScoped

import scalaj.http.HttpResponse;

/**
  * Created by francois on 14/03/17.
  */
@ScenarioScoped
class StepsData(
               var response:HttpResponse[_]
               )
{
  def this() = this(null)
}
