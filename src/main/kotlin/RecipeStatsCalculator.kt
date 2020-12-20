import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import java.io.FileInputStream
import java.io.InputStream
import java.io.InputStreamReader

class RecipeStatsCalculator {

    // https://www.acuriousanimal.com/blog/2015/10/23/reading-json-file-in-stream-mode-with-gson
    // https://sites.google.com/site/gson/streaming
    // Using a ```mixed mode between stream and object model``` of Gson streaming API
    fun calculateStats(filePath: String): ExpectedOutput {

        val inputStream: InputStream = FileInputStream(filePath)
        val reader = JsonReader(InputStreamReader(inputStream, "UTF-8"))

        reader.beginArray()
        while (reader.hasNext()) {
            val recipeData = Gson().fromJson<RecipeData>(reader, RecipeData::class.java)
            println(recipeData)
        }

        return ExpectedOutput(uniqueRecipeCount = 0)
    }
}

data class RecipeData(val postcode: String, val recipe: String, val delivery: String)

data class CustomPostcodeDeliveryTime(
    val postcode: String,
    val From: Int,
    val To: Int
)

data class ExpectedOutput(
    val uniqueRecipeCount: Int,
    /*val sortedRecipesCount: List<CountPerRecipe>,
    val busiestPostcode: BusiestPostcode,
    val countPerPostcodeAndTime: CountPerPostcodeAndTime,
    // https://stackoverflow.com/questions/57249356/kotlin-array-property-in-data-class-error
    val sortedRecipeNames: List<String> = listOf()*/
)

data class CountPerRecipe(
    val recipe: String,
    val count: Int,
)

data class BusiestPostcode(
    val postcode: String,
    val deliveryCount: Int,
)

data class CountPerPostcodeAndTime(
    val postcode: String,
    val fromAM: String,
    val toPM: String,
    val deliveryCount: Int,
)

data class CountPerPostcode(
    val postcode: String,
    val count: Int,
)