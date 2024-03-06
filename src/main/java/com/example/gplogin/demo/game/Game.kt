package com.example.gplogin.demo.game


class Cell(var name: String, var x: Int, var y: Int, var remove: Boolean)

class Game {

    private val map = ArrayList<List<Cell>>()
    private val width = 10
    private val height = 14

    private val action = ArrayList<Int>()

    private fun tryCell(): Boolean {


    }

    private fun tryCell(cell: Cell): Boolean {
        // 检查是否成功
        val success = map.any { it.any { c -> !c.remove } }
        if (success) {
            println("success")
            return true
        }

        for (i in 1 until width - cell.x) {
            val cells = moveToRight(cell, i)
            if (cells.isEmpty()) {
                break
            }

            val fCell = findRelativeCell(cell)
            if (fCell != null) {
                cell.remove = true
                fCell.remove = true
                tryCell()
                cell.remove = false
                fCell.remove = false
            }

            for (c in cells) {
                c.x -= i
            }
        }

    }

    private fun moveToRight(cell: Cell, offset: Int): List<Cell> {
        val cells = ArrayList<Cell>()
        cells.add(cell)
        for (i in cell.x + 1 until width) {
            val rCell = map[i][cell.y]
            if (rCell.remove) {
                break
            }
            cells.add(rCell)
        }
        val lastCell = cells.last()
        if (lastCell.x + offset >= width) {
            return emptyList()
        }
        for (c in cells) {
            c.x += offset
        }
        return cells
    }

    private fun findRelativeCell(cell: Cell): Cell? {
        val leftCell = cell.getLeft()
        if (leftCell?.name == cell.name) {
            return leftCell
        }
        val topCell = cell.getTop()
        if (topCell?.name == cell.name) {
            return topCell
        }
        val rightCell = cell.getRight()
        if (rightCell?.name == cell.name) {
            return rightCell
        }
        val bottomCell = cell.getBottom()
        if (bottomCell?.name == cell.name) {
            return bottomCell
        }
        return null
    }

    private fun Cell.getLeft(): Cell? {
        for (i in 1..x) {
            val cell = map[x - i][y]
            if (!cell.remove) {
                return cell
            }
        }
        return null
    }

    private fun Cell.getTop(): Cell? {
        for (i in 1..y) {
            val cell = map[x][y - i]
            if (!cell.remove) {
                return cell
            }
        }
        return null
    }

    private fun Cell.getRight(): Cell? {
        for (i in x + 1 until width) {
            val cell = map[i][y]
            if (!cell.remove) {
                return cell
            }
        }
        return null
    }

    private fun Cell.getBottom(): Cell? {
        for (i in y + 1 until height) {
            val cell = map[x][i]
            if (!cell.remove) {
                return cell
            }
        }
        return null
    }
}


