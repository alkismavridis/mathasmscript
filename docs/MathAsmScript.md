# Math asm Script

Or mas for short. It is a scripting language for defining
axioms and proving theorems, following the Math Asm paradigm.


## Statments

- package statement

The fist statement in a mas script should be the  pacakge statement.
All statements belong to a package. Packages can be also nested in other packages.

The package statement defines the package where all the statements will be saved.
Package names might consist only by lowercase letters, numbers and underscores.
Subpackages are separated by a dot.
Example:

``` package foo.bar```


- axiom definition statement
This way we can define axioms. Axiom names should only consist of capitalized
letters, numbers and underscores.

Example:

```axiom MY_AXIOM = "( 1 + 1 ) <---> ( 3 - 1 )" ```

Please note that every word in the axiom string should be 
separated by a white space. If no white space is present,
the symbols are considered as one word.

- import statement

 Examples:
 ```
import foo.bar.Stmt
import foo.bar.Stmt as Statement
import foo.bar { Stmt1, Stmt2 }
import foo.bar { Stmt1 as SomeLocalName, Stmt2 }



```
 
 - theorem definition statement
 
 // TODO
 
 - export theorem statement
 
 // TODO
 
 
 ## Theorem expressions
 A theorem expression starts with a reference to an existing statement,
 followed by a number of transformations that look like that:
 
 ```
theorem MyTheorem = FOO
    .cloneLeft()
    .all(BAR)
    .right(zoo)
    .leftOne(something)
```

Please note that the statement arguments in the parenthesis
can also be (temporary) theorem expressions. For example:

```
theorem MyTheorem = FOO
    .cloneLeft()
    .all( BAR.reverse() )
    .right( zoo.leftOne(stmt1).all(stmt2) )
    .leftOne(something)
```

Feel free to nest as many layers as you find meaningful
and readable.
Create temporary theorem variables for the rest.


### Functions of theorem expressions:
The following operations can be performed on a theorem expression.
Please note that the operation must be legal according to MathAsm paradigm.
An error will be produced otherwise.

Also, note that **theorems are immutable** once they are assigned to a variable.
The various modification methods will always create copies of the
referenced original statement.

- **all(base: Statement)**: Replaces all occurrences of base in the current theorem

- **right(base: Statement, position: Int?)**:
If the optional **position** parameter is missing, this replaces all
occurrences of base on the right side of the current theorem.
If position it is provided, a single replacement in the given position
will be performed. The position index is zero based.

- **left(base: Statement, position: Int?)**:
Same as right(), but operates on the left side.

- **reverse()**:
Reverses the statement.

- **cloneLeft()**:
Creates a new statement using the left side of the original statement

- **cloneRight()**:
Creates a new statement using the right side of the original statement

