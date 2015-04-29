# Mini-Language

The language can be customized by modifying the Token enum values.  
## Constructs
Changeable Token values are in italics.
### Input/Output
<pre>
<i>read</i> x
<i>write</i> x
</pre>  
### Conditional
<pre>
<i>if</i> condition <i>{</i>  
&nbsp;&nbsp;&nbsp;&nbsp;code  
<i>}</i>
</pre>
### Iterative
#### For loop
<pre>
<i>for</i> varname, value, expression <i>{</i>
&nbsp;&nbsp;&nbsp;&nbsp;code
<i>}</i>
</pre>
This loop creates a new variable(`varname`), that is incremented or decremented. The variable is compared to `value`. If `expression` is bigger or equal to 0, then the variable is incremented and the comparison uses `<=` every iteration, otherwise it is decremented and the comparison uses `>` every iteration.
#### While loop
<pre>
<i>while</i> condition <i>{</i>
&nbsp;&nbsp;&nbsp;&nbsp;code
<i>}</i>
</pre>
#### Do..while loop
<pre>
<i>do</i>  
  code
<i>while</i> condition
</pre>  
In do..while: The condition is evaluated, inverted, then checked for true.
### Variable declarations
Variables can be declared by using a new name before the assignment operator
<pre>x <i>=</i> 10</pre>  
or by using the new name after an input token  
<pre><i>read</i> x</pre>
### Assignment
A newline is necessary after assigning values.  
Valid:  
<pre><i>if true {</i>
&nbsp;&nbsp;&nbsp;&nbsp;n = 19
<i>}</i>
</pre>
<pre><i>if true {</i> n <i>=</i> 19
<i>}</i>
</pre>
<pre><i>if true {</i>  

n <i>=</i> 19

<i>}</i>
</pre>  
Invalid:  
<pre><i>if true {</i>n <i>=</i> 19<i>}</i></pre>
<pre><i>if true {</i>n <i>=</i> 19      <i>}</i></pre>
### Condition expressions  
They can resolve to boolean, numeric or even string values. JavaScript accepts all of them.  

JS falsy values:  
- `false`
- `0`
- `""`
- `''`
- `undefined`
- `null`

JS truey values:  
- `true`
- `1` (any non-`0` number)
- `" "` `' '` (any non-empty string)
  - Note: `"0"` is `true`, and so are `"false"`, `"null"`, `"undefined"`  

## Token configs
Tokens are specified with the following syntax:  
<pre><i>ENUM_NAME</i> @ <i>ENUM_VALUE</i></pre>
Each token has to be on its own line.
Note that `STRING`, `NUMBER`, `VARIABLE_NAME` and `LINE` do not use the specified values, as such anything will work.

## UI
The first text area should contain the pseudocode. The second area should contain the custom tokens. Leaving it empty will use the defaults from Token. The third one will contain the outputed JavaScript.  
In order to use file paths instead of direct code, append `file:///` before the input, output, and Token config file names. The input and output should be in the first area on 2 lines, and the Token config in the second.
