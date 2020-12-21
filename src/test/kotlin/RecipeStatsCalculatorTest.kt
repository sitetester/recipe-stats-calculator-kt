import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RecipeStatsCalculatorTest {

    private lateinit var expectedOutput: ExpectedOutput

    @BeforeAll
    fun initAll() {
        val name = "hf_test_calculation_fixtures_SMALL.json"
        val path = File(ClassLoader.getSystemResource(name).file).absolutePath

        val customPostcodeDeliveryTime = CustomPostcodeDeliveryTime("10120", 10, 3)
        expectedOutput =
            RecipeStatsCalculator(
                customPostcodeDeliveryTime,
                mutableListOf("Potato", "Veggie", "Mushroom")
            ).calculateStats(path)
    }

    @Test
    fun `Count the number of unique recipe names`() {
        assertEquals(4, expectedOutput.uniqueRecipeCount)
    }

    @Test
    fun `Count the number of occurrences for each unique recipe name (alphabetically ordered by recipe name`() {
        val sortedRecipesCount = expectedOutput.sortedRecipesCount

        // first recipe in sorted order
        assertEquals(CountPerRecipe("A5 Balsamic Veggie Chops", 1), sortedRecipesCount[0])

        // it should cover all recipes
        assertEquals(5, sortedRecipesCount.size)

        // `Creamy Dill Chicken` has two counts
        assertEquals(CountPerRecipe("Creamy Dill Chicken", 2), sortedRecipesCount[3])
    }

    @Test
    fun `Find the postcode with most delivered recipes`() {
        assertEquals(BusiestPostcode("10120", 3), expectedOutput.busiestPostcode)
    }

    @Test
    fun `Count the number of deliveries to postcode 10120 that lie within the delivery time between 10AM and 3PM`() {
        assertEquals(CountPerPostcodeAndTime("10120", 10, 3, 2), expectedOutput.countPerPostcodeAndTime)
    }

    @Test
    fun `List the recipe names (alphabetically ordered) that contain in their name one of the words (Potato, Veggie, Mushroom)`() {
        val filteredRecipeNames: MutableList<String> = mutableListOf(
            "A5 Balsamic Veggie Chops",
            "Balsamic Potato Pork Chops",
            "Speedy Steak Mushroom Fajitas"
        )
        assertEquals(filteredRecipeNames, expectedOutput.filteredRecipeNames)
    }
}