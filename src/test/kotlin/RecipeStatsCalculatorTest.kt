import java.io.File
import kotlin.test.Test

class RecipeStatsCalculatorTest {

    @Test
    fun `Count the number of unique recipe names`() {

        val name = "hf_test_calculation_fixtures_SMALL.json"
        val path = File(ClassLoader.getSystemResource(name).file).absolutePath

        assert(RecipeStatsCalculator().calculateStats(path).uniqueRecipeCount == 0)
    }
}