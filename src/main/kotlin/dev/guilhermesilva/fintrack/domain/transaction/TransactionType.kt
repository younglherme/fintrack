package dev.guilhermesilva.fintrack.domain.transaction

enum class TransactionType(val label: String) {
    INCOME("Receita"),
    EXPENSE("Despesa")
}