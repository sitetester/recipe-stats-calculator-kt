import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.stream.JsonReader
import java.io.FileInputStream
import java.io.InputStream
import java.io.InputStreamReader

class RecipeStatsCalculator(
    private val customPostcodeDeliveryTime: CustomPostcodeDeliveryTime,
    private val customWords: List<String> = listOf()
) {

    // https://www.acuriousanimal.com/blog/2015/10/23/reading-json-file-in-stream-mode-with-gson
    // https://sites.google.com/site/gson/streaming
    // Using a ```mixed mode between stream and object model``` of Gson streaming API
    fun calculateStats(filePath: String): ExpectedOutput {

        val inputStream: InputStream = FileInputStream(filePath)
        val reader = JsonReader(InputStreamReader(inputStream, "UTF-8"))

        val countPerRecipe: MutableMap<String, Int> = mutableMapOf()
        val countPerPostcode: MutableMap<String, Int> = mutableMapOf()
        val deliveriesCountPerPostcode: MutableMap<String, Int> = mutableMapOf()
        val filteredRecipeNames: MutableList<String> = mutableListOf()

        reader.beginArray()
        while (reader.hasNext()) {
            val recipeData = Gson().fromJson<RecipeData>(reader, RecipeData::class.java)

            calculateCountPer(recipeData.recipe, countPerRecipe)
            calculateCountPer(recipeData.postcode, countPerPostcode)
            calculateDeliveriesCountPerPostcode(recipeData, deliveriesCountPerPostcode)
            filterRecipeNameByCustomWords(recipeData.recipe, filteredRecipeNames)
        }

        return ExpectedOutput(
            uniqueRecipeCount = getUniqueRecipeCount(countPerRecipe),
            sortedRecipesCount = getSortedRecipeCount(countPerRecipe),
            busiestPostcode = getBusiestPostcode(countPerPostcode),
            countPerPostcodeAndTime = CountPerPostcodeAndTime(
                customPostcodeDeliveryTime.postcode,
                customPostcodeDeliveryTime.from,
                customPostcodeDeliveryTime.to,
                deliveriesCountPerPostcode[customPostcodeDeliveryTime.postcode]
            ),
            filteredRecipeNames = filteredRecipeNames.sorted()
        )
    }

    // counts the number of unique recipe names
    private fun getUniqueRecipeCount(countPerRecipe: MutableMap<String, Int>): Int {

        return countPerRecipe.filter { it.value == 1 }.count()
    }

    // counts the number of occurrences for each unique recipe name (alphabetically ordered by recipe name)
    private fun getSortedRecipeCount(countPerRecipe: MutableMap<String, Int>): List<CountPerRecipe> {

        return countPerRecipe.toSortedMap().map {
            CountPerRecipe(it.key, it.value)
        }
    }

    // finds the postcode with most delivered recipes
    private fun getBusiestPostcode(countPerPostcode: MutableMap<String, Int>): BusiestPostcode {

        val lastPair = countPerPostcode.toList().maxByOrNull { (_, value) -> value }!!
        return BusiestPostcode(lastPair.first, lastPair.second)
    }

    private fun filterRecipeNameByCustomWords(recipe: String, filteredRecipeNames: MutableList<String>) {

        customWords.forEach { customWord ->
            if (recipe.contains(customWord, true) && !filteredRecipeNames.contains(recipe)) {
                filteredRecipeNames.add(recipe)
            }
        }
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
    @SerializedName("unique_recipe_count") val uniqueRecipeCount: Int,
    @SerializedName("count_per_recipe") val sortedRecipesCount: List<CountPerRecipe>,
    @SerializedName("busiest_postcode") val busiestPostcode: BusiestPostcode,
    @SerializedName("count_per_postcode_and_time") val countPerPostcodeAndTime: CountPerPostcodeAndTime,
    // https://stackoverflow.com/questions/57249356/kotlin-array-property-in-data-class-error
    @SerializedName("match_by_name") val filteredRecipeNames: List<String>
)

data class CountPerRecipe(
    val recipe: String,
    val count: Int,
)

data class BusiestPostcode(
    val postcode: String,
    @SerializedName("delivery_count") val deliveryCount: Int,
)

data class CountPerPostcodeAndTime(
    val postcode: String,
    val fromAM: Int,
    val toPM: Int,
    @SerializedName("delivery_count") val deliveryCount: Int?,
)

