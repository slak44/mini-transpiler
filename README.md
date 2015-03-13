# Mini-Language

The language can be customized by modifying the Token enum values.  
<h2>Constructs</h2>
Changeable Token values are in italics.
<h3>Input/Output</h3>		
<pre>
<i>read</i> x
<i>write</i> x
</pre>  
<h3>Conditional</h3>		
<pre>
<i>if</i> condition <i>{</i>  
&nbsp;&nbsp;&nbsp;&nbsp;code  
<i>}</i>
</pre>
<h3>Iterative</h3>	
<h4>While loop</h4>   
<pre>
<i>while</i> condition <i>{</i>   
&nbsp;&nbsp;&nbsp;&nbsp;code     
<i>}</i> 
</pre>
<h4>Do..while loop</h4>   
<pre>
<i>do</i>  
  code
<i>while</i> condition
</pre>
<h3>Variable declarations</h3>
Variables can be declared by using a new name before the assignment operator   
<pre>x <i>=</i> 10</pre>  
or by using the new name after an input token  
<pre><i>read</i> x</pre>
<h3>Assignment</h3>
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
<h3>Condition expressions</h3>  
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

<h2>Token configs</h2>
Tokens are specified with the following syntax:  
<pre><i>ENUM_NAME</i> @ <i>ENUM_VALUE</i></pre>
Each token has to be on its own line.
Note that `STRING`, `NUMBER`, `VARIABLE_NAME` and `LINE` do not use the specified values, as such anything will work.
  
<h2>UI</h2>
The first text area should contain the pseudocode. The second area should contain the custom tokens. Leaving it empty will use the defaults from Token. The third one will contain the outputed JavaScript.  
In order to use file paths instead of direct code, append `file:///` before the input, output, and Token config file names. The input and output should be in the first area on 2 lines, and the Token config in the second.
