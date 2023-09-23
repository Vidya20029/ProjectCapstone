import scalaj.http.{Http, HttpOptions}
import scala.util.{Success, Failure}
import java.util.Base64
import java.nio.charset.StandardCharsets
import play.api.libs.json._

// NOTE: you must manually set API_KEY below using information retrieved from your IBM Cloud account.

val API_KEY = "<your api key>"

// Get IAM service token
val iam_url = "https://iam.cloud.ibm.com/identity/token"
val iam_response = Http(iam_url).header("Content-Type", "application/x-www-form-urlencoded").header("Accept",
 "application/json").postForm(Seq("grant_type" -> "urn:ibm:params:oauth:grant-type:apikey",
  "apikey" -> API_KEY)).asString
val iamtoken_json: JsValue = Json.parse(iam_response.body)

val iamtoken = (iamtoken_json \ "access_token").asOpt[String] match {
	case Some(x) => x
	case None => ""
}

// TODO: manually define and pass list of values to be scored
val payload_scoring = Json.stringify(Json.toJson(Map("input_data" -> List(Map("fields" -> Json.toJson(List(list_of_input_fields)),
	 "values" -> Json.toJson(list_of_values_to_be_scored))))))

val scoring_url = "https://private.us-south.ml.cloud.ibm.com/ml/v4/deployments/831cd74a-ea62-418a-bc14-157fb35723cd/predictions?version=2021-05-01"

val response_scoring = Http(scoring_url).postData(payload_scoring).header("Content-Type",
 "application/json").header("Authorization", "Bearer " + iamtoken).option(HttpOptions.
	method("POST")).option(HttpOptions.connTimeout(10000)).option(HttpOptions.readTimeout(50000)).asString
println("scoring response")
println(response_scoring)
