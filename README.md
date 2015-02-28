# Mini-Language

The language can be customized by modifying the Token enum values.  
<h2>Constructs</h2>
Changeable Token values are in italics.
<h3>Input</h3>		
<pre><i>read</i> x</pre>  
<h3>Output</h3>
<pre><i>write</i> x</pre>  
<h3>Conditional</h3>		
<pre>
<i>if</i> condition <i>{</i>  
&nbsp;&nbsp;&nbsp;&nbsp;code  
<i>}</i>
</pre>
<h3>Iterative</h3>	
<pre>
<i>while</i> condition <i>{</i>   
&nbsp;&nbsp;&nbsp;&nbsp;code     
<i>}</i> 
</pre>
<h3>Variable declarations</h3>
Variables can be declared by using a new name before the assignment operator   
<pre>x = 10</pre>  
or by using the new name after an input token  
<pre><i>read</i> x</pre>
<h3>Assignment</h3>
A newline is necessary after assigning values.  
Valid:  
<pre><i>if true {</i>
&nbsp;&nbsp;&nbsp;&nbsp;n = 19
<i>}</i>
</pre>
<pre><i>if true {</i> n = 19
<i>}</i>
</pre>
<pre><i>if true {</i>  
  
n = 19
  
<i>}</i>
</pre>  
Invalid:  
<pre><i>if true {</i>n = 19<i>}</i></pre>
<pre><i>if true {</i>n = 19      <i>}</i></pre>
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
- `1` (any non-0 number)
- `" "` `' '` (any non-empty string)
  - Note: `"0"` is true, and so are `"false"`, `"null"`, `"undefined"`
