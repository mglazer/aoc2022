import java.lang.Math.abs

class Day7 {

    sealed interface FileSystemNode
    data class FileNode(val size: Int, val name: String) : FileSystemNode
    data class DirectoryNode(val nodes: MutableList<FileSystemNode>, val name: String, val parent: DirectoryNode?) :
        FileSystemNode {
        override fun toString(): String {
            return "$name + [$nodes]"
        }

        fun addFile(fileNode: FileNode) {
            val existingNode = nodes.find {
                when (it) {
                    is FileNode -> it.name == fileNode.name
                    else -> false
                }
            }

            if (existingNode == null) {
                nodes.add(fileNode)
            }
        }

        fun size(): Int {
            return nodes.sumOf { node ->
                when (node) {
                    is FileNode -> node.size
                    is DirectoryNode -> node.size()
                }
            }
        }
    }

    companion object {
        fun updateCurrentNode(currentNode: DirectoryNode?, target: String): DirectoryNode {
            val existingNode = currentNode?.nodes?.find {
                when (it) {
                    is FileNode -> it.name == target
                    is DirectoryNode -> it.name == target
                }
            }

            return if (existingNode == null) {
                val newNode = DirectoryNode(mutableListOf(), target, currentNode)
                currentNode?.nodes?.add(newNode)
                newNode
            } else {
                existingNode as DirectoryNode
            }
        }

        fun buildFileSystem(commands: List<String>): DirectoryNode {

            val rootNode = DirectoryNode(mutableListOf(), "/", null)
            var currentNode: DirectoryNode? = rootNode
            var handlingLs = false
            commands.forEach { command ->
                if (command.startsWith("$")) {
                    handlingLs = false;
                    val commandAndLocation = command.substring(2).split(" ");
                    if (commandAndLocation[0] == "ls") {
                        handlingLs = true
                    } else if (commandAndLocation[0] == "cd") {
                        handlingLs = false
                        val target = commandAndLocation[1]
                        if (target == "..") {
                            currentNode = checkNotNull(currentNode?.parent)
                        } else {
                            currentNode = updateCurrentNode(currentNode, target)
                        }
                    }
                } else if (handlingLs) {
                    if (command.startsWith("dir")) {
                        updateCurrentNode(currentNode, command.split(" ")[1])
                    } else {
                        val sizeAndFile = command.split(" ");
                        val size = sizeAndFile[0].toInt();
                        val file = sizeAndFile[1];

                        currentNode?.addFile(FileNode(size, file))
                    }
                } else {
                    println("Invalid state....${command}")
                }
            }
            return rootNode
        }
    }
}

private val input = """
$ cd /
$ ls
dir a
14848514 b.txt
8504156 c.dat
dir d
$ cd a
$ ls
dir e
29116 f
2557 g
62596 h.lst
$ cd e
$ ls
584 i
$ cd ..
$ cd ..
$ cd d
$ ls
4060174 j
8033020 d.log
5626152 d.ext
7214296 k
""".trim()

fun main() {
    val fileSystem = Day7.buildFileSystem(Utils.readLines("day7/input.txt"))

    val maxSize = 100000
    fun traverseNodes(node: Day7.FileSystemNode): List<Day7.FileSystemNode> {
        return when (node) {
            is Day7.FileNode -> listOf<Day7.FileSystemNode>()
            is Day7.DirectoryNode -> {
                if (node.size() <= maxSize) {
                    mutableListOf(node) + node.nodes.flatMap { traverseNodes(it) }
                } else {
                    node.nodes.flatMap { traverseNodes(it) }
                }
            }
        }
    }

    fun traverseNodesMin(node: Day7.FileSystemNode, requiredSize: Int): List<Day7.DirectoryNode> {
        return when (node) {
            is Day7.FileNode -> listOf<Day7.DirectoryNode>()
            is Day7.DirectoryNode -> {
                if (node.size() >= requiredSize) {
                    mutableListOf(node) + node.nodes.flatMap { traverseNodesMin(it, requiredSize) }
                } else {
                    node.nodes.flatMap { traverseNodesMin(it, requiredSize) }
                }
            }
        }
    }

    val maxedNodes = fileSystem.nodes.flatMap { traverseNodes(it) }

    println(maxedNodes.filterIsInstance(Day7.DirectoryNode::class.java).sumOf { it.size() })

    val requiredSize = 70000000
    val needed = kotlin.math.abs(requiredSize - 30000000 - fileSystem.size())
    println("Required Size: $requiredSize, Starting Size: ${fileSystem.size()}, Free Space = ${requiredSize - fileSystem.size()}) Needed = $needed")

    val deletionNodes = fileSystem.nodes.flatMap { traverseNodesMin(it, needed) }.sortedBy { it.size() }
    println(deletionNodes.first().size())

}

