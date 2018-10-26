package klearn

interface Model {
    fun fit(df: DataFrame, col: Column<*>)
    fun predict(data: DataFrame): Column<*>
}
