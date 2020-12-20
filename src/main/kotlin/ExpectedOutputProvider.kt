class ExpectedOutputProvider(private val countPerRecipe: MutableMap<String, Int>) {

    fun getExpectedOutput(): ExpectedOutput {

        return ExpectedOutput(
            uniqueRecipeCount = getUniqueRecipeCount()
        )
    }

    private fun getUniqueRecipeCount(): Int {

        return this.countPerRecipe.filter { it.value == 1 }.count()
    }
}