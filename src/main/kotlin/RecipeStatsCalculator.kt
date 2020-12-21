import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import java.io.FileInputStream
import java.io.InputStream
import java.io.InputStreamReader

class RecipeStatsCalculator(private val customPostcodeDeliveryTime: CustomPostcodeDeliveryTime) {

    // https://www.acuriousanimal.com/blog/2015/10/23/reading-json-file-in-stream-mode-with-gson
    // https://sites.google.com/site/gson/streaming
    // Using a ```mixed mode between stream and object model``` of Gson streaming API
    fun calculateStats(filePath: String): ExpectedOutput {

        val inputStream: InputStream = FileInputStream(filePath)
        val reader = JsonReader(InputStreamReader(inputStream, "UTF-8"))

        val countPerRecipe: MutableMap<String, Int> = mutableMapOf()
        val countPerPostcode: MutableMap<String, Int> = mutableMapOf()
        val deliveriesCountPerPostcode: MutableMap<String, Int> = mutableMapOf()

        reader.beginArray()
        while (reader.hasNext()) {
            val recipeData = Gson().fromJson<RecipeData>(reader, RecipeData::class.java)

            calculateCountPer(recipeData.recipe, countPerRecipe)
            calculateCountPer(recipeData.postcode, countPerPostcode)
            calculateDeliveriesCountPerPostcode(recipeData, deliveriesCountPerPostcode)
        }

        val expectedOutputProvider = ExpectedOutputProvider()

        return ExpectedOutput(
            uniqueRecipeCount = expectedOutputProvider.getUniqueRecipeCount(countPerRecipe),
            sortedRecipesCount = expectedOutputProvider.getSortedRecipeCount(countPerRecipe),
            busiestPostcode = expectedOutputProvider.getBusiestPostcode(countPerPostcode),
            countPerPostcodeAndTime = CountPerPostcodeAndTime(
                customPostcodeDeliveryTime.postcode,
                customPostcodeDeliveryTime.from,
                customPostcodeDeliveryTime.to,
                deliveriesCountPerPostcode[customPostcodeDeliveryTime.postcode]
            )
        )
    }

    private fun calculateCountPer(key: String, countPer: MutableMap<String, Int>) {

        val count = countPer[key]

        if (count != null) {
            countPer[key] = count + 1
        } else {
            countPer[key] = 1
        }
    }

    private fun calculateDeliveriesCountPerPostcode(
        recipeData: RecipeData,
        deliveriesCountPerPostcode: MutableMap<String, Int>
    ) {
        if (recipeData.postcode == this.customPostcodeDeliveryTime.postcode && isWithinDeliveryTime(recipeData.delivery)) {
            calculateCountPer(recipeData.postcode, deliveriesCountPerPostcode)
        }
    }

    private fun isWithinDeliveryTime(delivery: String): Boolean {

        val match = Regex("(\\d{0,2})AM\\s-\\s(\\d{0,2})PM").find(delivery)!!
        val (from, to) = match.destructured

        return from.toInt() >= this.customPostcodeDeliveryTime.from && to.toInt() <= this.customPostcodeDeliveryTime.to
    }
}

data class RecipeData(val postcode: String, val recipe: String, val delivery: String)

data class CustomPostcodeDeliveryTime(
    val postcode: String,
    val from: Int,
    val to: Int
)

data class ExpectedOutput(
    var uniqueRecipeCount: Int,
    val sortedRecipesCount: List<CountPerRecipe>,
    val busiestPostcode: BusiestPostcode,
    val countPerPostcodeAndTime: CountPerPostcodeAndTime,
    /*
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
    val fromAM: Int,
    val toPM: Int,
    val deliveryCount: Int?,
)

data class CountPerPostcode(
    val postcode: String,
    val count: Int,
)