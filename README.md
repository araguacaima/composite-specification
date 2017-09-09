![amma logo](/docs/resources/amma-logo.png "AmMa Logo") raguacaima
===

ToC
===
***

  * [Composite Specification Pattern](#composite-specification-pattern)
  * [Command](#command)
    * [Joining Composite Specification with Command Pattern](#joining-composite-specification-with-command-pattern)
  * [SpecificationMap and SpecificationMapUtil](#specificationmap-and-specificationmaputil)
  * [Interpreter](#interpreter)
  * [How to use it?](#how-to-use-it)
  * [Conclusion](#conclusion)


Composite Specification Pattern
===
***

The *Composite Specification pattern* is a particular implementation of the "Specification" design pattern, whereby business logic can be recombined by stringing business logic together with Boolean logic. Next we're going to dedicate a little more space to its detailed description.

The pattern describes a business logic unit that can be combined with other logical business units. In this pattern, a business logic unit inherits its functionality from the abstract class *CompositeSpecification* by _aggregation_. The class *CompositeSpecification* has a function called *_isSatisfiedBy_* that returns a Boolean value. After the instantiation, the specification is _"chained"_ to other specifications, making the new specifications easy to maintain besides offering to the business logic a high level of personalization. On the other hand the instantiation of business logic can, by means of method invocation or control inversion, alter its state to become a delegate of other classes.

Here is a class diagram of the basic pattern.

![alt text](https://github.com/adam-p/markdown-here/raw/master/src/common/images/icon48.png "Logo Title Text 1")

Based on the described pattern, an extended model is proposed to make a slight adaptation that would allow a greater flexibility for the recursive path of the nodes that make up the complete structure, as well as the eventual obtaining of the chain of associations that make up the expressions that represent the specifications. In addition, facilities were attached for the determination of the assessment of the condition to be invoked by the component classes. Thus, see the following adaptation to the diagram:




Note that the mandatory *_getLeftNode_*, *_getRightNode_*, and toString methods were included in the Specification interface. The AbstractSpecification abstract class only implements the *_toString_* method whose implementation is common to all objects inheriting from it and delegates to its daughters the implementation of the *_getLeftNode_* and *_getRightNode_* methods. Another important notice to note is that the signature of the *_isSatisfiedBy_* method was modified by adding a Map type parameter. This parameter simply serves as a bearer of any variables and data required for the evaluation of the condition by the daughter classes in addition as a communicator or messenger between the different states that the method could handle or modify and that would be of eventual utility to their super classes or to classes outside the pattern that use the same.

Command
===
***

The class diagram associated with the pattern is detailed below.





Joining Composite Specification with Command Pattern
---

The main intention to include this pattern is that it form a part of a solution that could replace the body of the methods of the classes that implemented the Commander Pattern (obviously it could have other uses). Replacement of the Commander interface could be implementations of the CompositeSpecification pattern. That's the way that the *_execute_* method of the Commander Pattern, would look like the following snippet:

```java
public Object<T> execute(<T> request) {

    SpecificationMap specificationMap = SpecificationMapUtil.getInstance(
    CommandExpressions.getSpecificationExpressions(), FooCommand.class);
    Specification specification = SpecificationMap.getSpecificationFromMethod("execute");
    return specification.isSatisfiedBy(request, map);
}
```

The difference is notorious. The function of "knowing" and implementing the business logic associated with the _command_ would no longer fall on it, but instead was delegated to a third party determination of the *_"expression"_* that would model the particular business logic for the command in question.

The determination of said expression was achieved after the implementation of the *Interpreter Pattern*, which will be described below. For simplicity in the detail of the use of this pattern in the solution, we can temporarily omit the explanation of the mechanism used to determine the particular business logic of a specific command. Let us suppose for a moment that the particular specification for this command is obtained by means of the lines of code above.

The implementation of the CompositeSpecification pattern makes use of some classes and utilitarian methods that facilitate the reading and interpretation of expressions to generate from them specifications that represent them. In particular the SpecificationMap and SpecificationMapUtil classes

Let's talk a little about these utilitary classes.

SpecificationMap and SpecificationMapUtil
===
***

The SpecificationMap class represents a specification map for each class where each map key associates a specific method of the class with a specific specification for the class. This class is obtained through the utility class SpecificationMapUtil which is a Singleton Map (Design Pattern). The way to get a particular instance of SpecificationMapUtil is to tell you which map to search and which class to search. Once the class is located within the map, a SpecificationMap object is then returned, which is constructed from an expression representing the specification for each method that owns the class in question.

This is how the above statement

```java
SpecificationMap specificationMap = 
  SpecificationMapUtil.getInstance(CommandExpressions.getSpecificationExpressions(), FooCommand.class);
```

try to get all the specifications for all the methods available for the UserAddCommand class. As we said previously, we have to abstract ourselves at this level of the class-associated expression retrieval mechanism, since this detail is not the responsibility of the pattern (it is delegated to the Interpreter pattern). In this way, the determination of the expression could be solved by a database query, by reading a file, by the invocation of a web service, in short, by any mechanism that is desired. The important thing is that you could get a string that represents the desired expression.

What was wanted then was to represent, through a specification, a sequence that implemented the business logic required by the class. The string that was obtained to conform to this specification had to make reference to the classes that were logically arranged to solve together the problem that wanted to solve the original command. Each of these classes would solve the portion of the business logic that corresponded to them within the sequence of invocations of the command. This is why the com.movistar class is passed as parameter to the SpecificationMapUtil instantiator. Provisioning.command.tbilling.UserAddCommand.

Since the specification consists of a logical tree structure of classes that will be instantiated and implement the "isSatisfiedBy" method, all classes are required to be "instantiable", that is; achievable by the ClassLoader at runtime. To ensure this, it is necessary that both the library containing the utility classes and the definition of the specification, as well as the one intended to use the SpecificationMapUtil class (ejb), reside together in the same ClassLoader domain that instantiates the running classes.

Passing the class as a parameter, instead of its name only (which would be the first possibility), would ensure that the string of instan- cations required by the SpecificationMapUtil builder could find the right classes and that, in addition, to the invoking class, could be used since these are always treated the same classes.

Well, once the map was obtained, the next step would be simply to obtain the specification corresponding to the method that interests us. In this particular we were interested in the "execute" method of the class.

```java
Specification specification = SpecificationMap.getSpecificationFromMethod("execute");
```

Finally, after obtaining it, it could proceed to evaluate that specification:  

```java
return specification.isSatisfiedBy(request, map);
```

We see here that the objective was achieved that the "execute" method does not have to know what business logic or other mechanisms exist, nor the chain of dependence between them. It should only concern itself with formatting the required data (request and map) and redirecting the response to the corresponding instance, logically considering a convenient treatment of the eventual exceptions that the recursive execution of the specification can throw.

Now we can have an improved version of the FooCommand class:

```java
public class FooCommand extends AbstractCommand {

	private static Specification specificationExecute;
	private static Specification specificationInitialize;
	private static Specification specificationPostExecute;

	static {
     SpecificationMap specificationMap;
     specificationMap = SpecificationMapUtil.getInstance(CommandExpressions.getSpecificationExpressions(), FooCommand.class);
		specificationExecute = specificationMap.getSpecificationFromMethod("execute");
		specificationInitialize = specificationMap.getSpecificationFromMethod("initialize");
		specificationPostExecute = specificationMap.getSpecificationFromMethod("postExecute");
    }

	public Specification getSpecificationExecute() {
		return specificationExecute;
	}
	public static void setSpecificationExecute(Specification specificationExecute) {
		FooCommand.specificationExecute = specificationExecute;
	}
	public Specification getSpecificationInitialize() {
		return specificationInitialize;
	}
	public static void setSpecificationInitialize(Specification specificationInitialize) {
		FooCommand.specificationInitialize = specificationInitialize;
	}
	public Specification getSpecificationPostExecute() {
		return specificationPostExecute;
	}
	public static void setSpecificationPostExecute(Specification specificationPostExecute) {
		FooCommand.specificationPostExecute = specificationPostExecute;
	}
	protected void setSpecificationInitialize(Collection commandSpecificationsExpression) {
		specificationInitialize = buildSpecificationFromCollection
									(commandSpecificationsExpression, "initialize");
	}
	protected void setSpecificationExecute(Collection commandSpecificationsExpression) {
		specificationExecute = buildSpecificationFromCollection
									(commandSpecificationsExpression, "execute");
	}
	protected void setSpecificationPostExecute(Collection commandSpecificationsExpression) {
		specificationPostExecute = buildSpecificationFromCollection
									(commandSpecificationsExpression, "postExecute");
	}    
}
```

AbstractCommand class could be declared as follows:

```java
public abstract class AbstractCommand<T> implements CommandInterface {

	 . . . . . 

    public void initialize(T request) throws ProvisioningException {
		  Boolean result;
        try {
            result = Boolean.valueOf(getSpecificationInitialize().isSatisfiedBy(request, map));
        } catch (Exception e) {
            // Exception treatment
        }
    }

    public Object execute (T request) throws ProvisioningException {
		  Boolean result;
        try {
            result = Boolean.valueOf(getSpecificationExecute().isSatisfiedBy(request, map));
        } catch (Exception e) {
            // Exception treatment
        }
			return result;
    }

    public Object postExecute (T request) throws ProvisioningException {
		  Boolean result;
        try {
            result = Boolean.valueOf(getSpecificationPostExecute().isSatisfiedBy(request, map));
        } catch (Exception e) {
            // Exception treatment
        }
			return result;
    }

    protected abstract Specification getSpecificationInitialize();
    protected abstract Specification getSpecificationExecute();
    protected abstract Specification getSpecificationPostExecute();
    protected abstract void setSpecificationInitialize(
								Collection commandSpecificationsExpression);
    protected abstract void setSpecificationExecute(
								Collection commandSpecificationsExpression);
    protected abstract void setSpecificationPostExecute(
								Collection commandSpecificationsExpression);

    public void setSpecification(final Collection commandSpecificationsExpression,
                                 final String methodNameSuffix) {
		. . . .
    }
    protected Specification buildSpecificationFromCollection
								(Collection commandSpecificationsExpressionForMethod,
                                  String methodNameSuffix) {
		. . . .
    }
}
```

The convenient delegation of the details of common implementation of all the "commands" to an abstract class provides uniformity to the development and grants a high level of scalability. The implementation of the execute, initialize and postExecute methods are, as noted, basically the same, since it is the determination of the specification (via the expression delegated to the pattern Interpreter), who would direct the business logic to be executed for each command.

Let us then describe the last pattern involved in the solution, the Interpreter pattern.


Interpreter
===
***

The Interpreter is a design pattern that, given a language, defines a representation for its grammar along with a language interpreter.
It is used to define a language or to represent regular expressions that represent strings to look for within other strings. In addition, in general, to define a language that allows to represent the different instances of a family of problems.

The class diagram associated with the pattern is detailed below.





As in the previous pattern, the pattern was considerably refined in such a way as to extend its possibilities and to support even more the proposed solution.

Basically, facilities were added for the evaluation of various expressions beyond simple syntactic interpretation. This evaluation involves the adoption of implementations of the pattern applied to different subsets of language elements, on the one hand a logical set and on the other an arithmetic set. In this way the adaptation allows the interpretation and evaluation of arithmetic and logical expressions. To achieve this end, the specification of an "Evaluator" and an "Expression to the employer" was included. Such evaluator may assist the Interpreter to "evaluate" the context-dependent "expression" that is required. The following is a detail of the associated class diagram.





As you will notice these implementations and adaptations are not tied to a specific business logic. This approximation attends to a functionality available for its use by any interested third-party. For this reason, both adaptations of the described patterns are available as part of this utility libraries, in which a set of common and platform-independent functionalities and/or specific solutions are available.


How to use it?
===
***

The central point of the proposal that the solution be "configurable" was framed in the fact that it was possible to find an expression that could represent the combination that best fit the particular scenario of a given moment and that reflects the required business logic . This expression should be "interpreted" and should also describe the orderly execution of activities that would equate the sequential actions implemented in each of the commands. If we take as base the class FooCommand then we can create an example of expression as mentioned.

The following would be the representation of a "key, value" pair stored in a properties file (it could have been in database or obtained through a web service, for example):

```yaml
com.foo.FooCommand.execute=
com.foo.business.rules.Condition1 & com.foo.business.rules.Condition2 & (com.foo.business.rules.Condition3 | com.foo.business.rules.Condition4)
```

com.foo.FooCommand is the command implementation which is going to delegate its "execute" method to a specification chain described by
com.foo.business.rules.Condition1, com.foo.business.rules.Condition2, com.foo.business.rules.Condition3 and com.foo.business.rules.Condition4. The last are all instances of Specification. Symbols like & and | refer to the logical operators AND and OR respectively and the parentheses allow to make logical groupings for the Interpreter pattern.

The interpretation of this pair would be something like: "The classes described in the 'value' will be considered to be instantiated as part of a specification applicable to the class and method described by the 'key'". Thus, the UserAddCommand class in its execute method must then consider the rules described by the 'value' of said 'key' to carry out its operations, which are:

* It runs first com.foo.business.rules.Condition1
* If successful, it runs com.foo.business.rules.Condition2
* If they are successful they execute com.foo.business.rules.Condition3 or com.foo.business.rules.Condition4
* The logical evaluation of the whole expression is returned


Conclusion
===
***

It is easy to notice the potential that we have then to define different rules, the possibilities are as many as are required. All combinations and arrangements between these elements are possible. Any omissions that you wish to make at will are also very simple to carry out. Basically it is required to remove the desired persistence orchestrator. It is also clear that the inclusion of a new element of persistence is greatly simplified.

Correct handling of the logic that would ensure orderly and consistent execution of the rule described would be delegated to the CompositeSpecification pattern described above and not to the body of the execute methods of the Command pattern implementers, as already specified. This way the instantiation and execution of the invokers of the commands would be made uniform. Independence of the handling of persistence operations by one command (and method) or another through a simple configuration was also achieved.

After determining the expression that reflects the business rule it was posbile then creating, from it, a "specification" that would model it more concretely.





