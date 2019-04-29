# 1 基础

## 1.1 const constexpr

- const: meaning roughly ‘‘I promise not to change this value’’ . This is used primarily

to specify interfaces(接口使用), so that data can be passed to functions without fear of it being modified.
The compiler enforces the promise made by const.

- constexpr: meaning roughly ‘‘to be evaluated at compile time（编译时）’’ . This is used primarily

to specify constants, to allow placement of data in memory where it is unlikely to be corrupted,
and for performance.

## 1.2 implementation-defined 

国外学者用的，implementation 可以认为是编译器，或者实现

## 1.3 初始化列表

初始化列表，提供了检查的功能，不允许 narrowing 操作，可以扩展操作

```c++
bool b_test{7} 这个编译报错，原因是，narrowing。窄化
error C2397: 从“int”转换到“bool”需要收缩转换
```

## 1.4 基本数据类型

### 1.4.1 bool

true 的值为 1， false 值 为 0 。非零的值都认为是 1 。

```c++
bool b1 = 7; // 7!=0, so b becomes true
bool b2 {7}; // error : narrowing 做了检查的，编译不过

```

### 1.4.2 字符型

`char`：我在 win 10 上测试，超过 128 会出问题

`signed char`：[-128, 127]  

`unsigned char`:[0, 255]

`wchar_t`: Provided to hold characters of a larger character set such as Unicode (see §7.3.2.2).
The size of wchar_t is implementation-defined and large enough to hold the largest character
set supported by the implementation’s locale (Chapter 39).
`char16_t`: A type for holding 16-bit character sets, such as UTF-16.
`char32_t`: A type for holding 32-bit character sets, such as UTF-32.

### 1.4.3 整形

`int`:  `long int`:   `long long int`:

### 1.4.4 浮点型

`float`:   `double`:   `long double`:

### 1.4.5 struct

struct有默认构造函数，一旦定义了非空构造函数，必须使用构造函数，否则无法默认构造。

```C++
struct Address{
	const char* name;
	int number;
	const char* street;
	const char* town;
	char state[2];
	const char* zip;
};

Address aj = {
		"Jim Dandy", 61, "South St",
		"new pro", { 'N', 'J' }, "09747"
	};

Address aj2;
aj2.name = "wudeyun";
aj2.number = 12;
```



## 1.5 undefined  unspecified 

 undefined 通常是那些语法正确,但行为未知,且不可预知(或行为缺乏广泛性)的东西.比如a[-1]的值.使用已经析构的对象等等.

 unspecified 通常是满足了标准需求以外的行为.比如类的布局,虚函数的实现方式,一些表达式的计算顺序等等. 

## 1.6 与 C 兼容

每个 C 标准库头文件，都有一个与之兼容的 C++ 头文件。比如 stdio.h 对应的是 cstdio

`cstdio`文件中：将 printf 定义在 std 空间中。

```c++

```

为了兼容 C ， `stdio.h` 中

```c++
#ifdef __cplusplus // for C++ compilers only (§15.2.5)
namespace std { // the standard librar y is defined in namespace std (§4.1.2)
extern "C" { // stdio functions have C linkage (§15.2.5)
#endif
/* ... */
int printf(const char∗, ...);
/* ... */
#ifdef __cplusplus
}
}
// ...
using std::printf; // make printf available in global namespace
// ...
#endif
```

这样，当我们引用 stdio.h 后，实际的实现是 cstdio 实现的， stdio.h 只是做了一层包装，从而打到兼容 C 程序的目的

想这样的情况:一个库文件已经用C写好了而且运行得很良好，这个时候我们需要使用这个库文件，但是我们需要使用C++来写这个新的代码。如果这个代码使用的是C++的方式链接这个C库文件的话，那么就会出现链接错误.我们来看一段代码:首先，我们使用C的处理方式来写一个函数，也就是说假设这个函数当时是用C写成的:

```c
//f1.c
void f1() {return; }
```

编译命令是:gcc -c f1.c -o f1.o 产生了一个叫f1.o的库文件。再写一段代码调用这个f1函数:

//这个extern表示f1函数在别的地方定义，这样可以通过编译，但是链接的时候还是需要链接上原来的库文件.

```c++
// test.cpp
extern void f1();

int main(){ 
    f1();
	return 0;
}
```

通过gcc -c test.cxx -o test.o 产生一个叫test.o的文件。然后，我们使用gcc test.o f1.o来链接两个文件，可是出错了，错误的提示是:

test.o(.text + 0x1f):test.cpp: undefine reference to 'f1()'

也就是说，在编译test.cxx的时候编译器是使用C++的方式来处理f1()函数的，但是实际上链接的库文件却是用C的方式来处理函数的，所以就会出现链接过不去的错误:因为[链接器](https://baike.baidu.com/item/%E9%93%BE%E6%8E%A5%E5%99%A8)找不到函数。

因此，为了在C++代码中调用用C写成的库文件，就需要用extern "C"来告诉编译器:这是一个用C写成的库文件，请用C的方式来链接它们。

比如，现在我们有了一个C库文件，它的头文件是f.h，产生的lib文件是f.lib，那么我们如果要在C++中使用这个库文件，我们需要这样写:

```c++
extern "C"
{
	#include "f.h"
}
```

回到上面的问题，如果要改正链接错误，我们需要这样子改写test.cxx:

```c++
extern "C"
{
	extern void f1();
}

int main()
{
	f1();
	return 0;
}
```

重新编译并且链接就可以过去了.

## 1.7 单一定义规则 

An object must be defined exactly once in a program. It may be declared many times, but
the types must agree exactly

c++在链接的过程中，会对符号进行重定向，多次定义一个变量，会造成重复定义，如下 x 被定义了两次，b 申明的类型不一样，c 没有申明。

```c++
//file1.cpp
    int x = 1;
	int b = 1;
	extern int c;

//file2.cpp
	int x;
	extern double b;
	extern int c;
```

## 1.8 JAVA 和 C++ 区别

- java 所有变量都定义在一个范围类，或者所有的变量都在类的范围中定义
- \#include 跟 import 不一样， import 只能用一次， #include 可以多次导致重复
- C++ 单一定义原则是妥协的，类的定义本身就违反了 c 的单一定义原则， 基本类型只能申明一次，但是 类可以多次定义。
- c++ 的static、const表示内联，而java 得static表示全局变量，恰恰相反
- c++类中的static表示共有，static函数表示共有

## 1.9 先申明，后使用

```c++
// file1.cpp:
int g() { return f()+7; } // error : f() not (yet) declared
int f() { return x; } // error : x not (yet) declared
int x;
```

## 1.10 内部链接，外部链接

这个我的理解是与 **链接** 紧密相连的，内部链接的意思是链接的时候，使用内部变量，外部链接是使用外部变量

const 和 static 表示内联，是不能使用 extern 引用的，如下都是错误的。

```c++
//file1.cpp
	static int x = 1;
	const int y = 1;

//file2.cpp
	extern int x;
	extern const int y;

//但是下面这个却是正确的
// file1.cpp:
extern const int a = 77;
// file2.cpp:
extern const int a;
```

对于使用 **inline** 关键字的函数，An inline function must be defined identically（完全一样） in every translation unit（编译单元） in which it is used，inline[关键字](https://baike.baidu.com/item/%E5%85%B3%E9%94%AE%E5%AD%97)用来定义一个类的[内联函数](https://baike.baidu.com/item/%E5%86%85%E8%81%94%E5%87%BD%E6%95%B0)，引入它的主要原因是用它替代C中[表达式](https://baike.baidu.com/item/%E8%A1%A8%E8%BE%BE%E5%BC%8F)形式的[宏定义](https://baike.baidu.com/item/%E5%AE%8F%E5%AE%9A%E4%B9%89)。 inline只有在定义的时候使用，而不是申明的时候使用。内联函数会自动展开，过大的函数不适合。内联的函数不能被 extern 引用

```c++
// file1.cpp:
inline int f(int i) { return i; }
// file2.cpp:
inline int f(int i) { return i+1; }
```

这个违反了完全一致的原则，所以使用的时候，inline 函数最好放在 .h 文件中定义，这样就会正常

## 1.11 引用

引用和普通变量是严格区分的，引用赋值给普通变量是复制赋值。
//返回值，会生成临时量来存储 temp
//必须等函数调用语句结束后，临时量销毁
float fn1(float r)
{
	temp = r*r*3.14;
	return temp;
}

//返回引用，返回的是对 temp 的引用
//不产生临时变量
float& fn2(float r)
{
	temp = r*r*3.14;
	return temp;
}

void main()
{
	float a=fn1(5.0);  //临时量赋值给a
	float& b=fn1(5.0); //引用临时量
	float c=fn2(5.0);  //引用赋值给c，安全
	//引用一个变量的引用危险，万一这个变量是
	//临时量，在这个语句执行后，临时量会被销毁
	float& d=fn2(5.0); 
}

## 1.12 空指针 0 \\0的问题

**空指针**：C 的空指针是 (void*) 0 ，首先它是一个指向0的指针类型。C++的空指针以前是数字0，在C++11中是nullptr，是数字0显然有歧义，已经放弃了。在VS 2013的库文件string.h中可以看到如果定义。

```
1 /* Define NULL pointer value */
2 #ifndef NULL
3 #ifdef __cplusplus
4 #define NULL    0
5 #else  /* __cplusplus */
6 #define NULL    ((void *)0)
7 #endif  /* __cplusplus */
8 #endif  /* NULL */
```

在C语言中，“当常量0处于应该作为指针使用的上下文中时，它就作为空指针使用”（《征服C指针》）。例如，下边的指针定义和初始化是没问题的（即没警告也没报错），这里用的是数字 0 ，它不是字符 0。C 语言隐式转化为(void*) 0 

```
 int * p = 0;    /* C language */
```

​     但如果定义成如下的样子呢？C 语言隐士转换失效

```
 int * p = 3;    /* C language */
```

　　这一句可以编译通过，但在VS 2013中有这样的警告：“warning C4047: “初始化”:“int *”与“int”的间接级别不同”。

　　我又试了一下这一句在C++中的情况，VS 2013就直接报错了：“ ‘int’ 类型的值不能用于初始化 ‘int *’ 类型的实体”。C++必须要显式转换。c++11中重新定义了nullptr关键字，表示空指针。

　　**2.** ‘\0’：它的ASCII码值为0。注意它与空格' '（ASCII码值为32）及'0'（ASCII码值为48）不一样的。

　　在《征服C指针》中，作者还提到了一种错误的程序写法：使用NULL来结束字符串。例如下边的程序就是有问题的：NULL被定义为指向0的指针，为指针类型。

```
char str[4] = { '1', '2', '3', NULL };    /* C language */
```

　　在VS 2013中，会的这样的警告：“warning C4047: “初始化”:“char”与“void *”的间接级别不同”。而在C++中，这一句是没有问题的。

　　还有一点值得注意，如下的程序在C/C++中都是没有问题的：

```
char str[4] = { '1', '2', '3', 0 };    / C/C++ language */
```

　　但为了防止混淆，在C/C++中，当要给一个字符串添加结束标志时，都应该用‘\0’而不是NULL或0。

## 1.13 回车换行

当按下回车键时，在标准输入缓存中，会多一个<font color=red>回车键</font>。windows系统在进行文件操作时，会将这个回车键转化为两个字符<font color=red>回车和换行</font>再写入文件中，而linux系统不会。读出时相反。



# 2 抽象机制

## 2.1 copy 初始化

```c++
Date d1 = my_bir thday; // initialization by copy
Date d2 {my_bir thday}; // initialization by copy
```

[![复制代码](http://common.cnblogs.com/images/copycode.gif)](javascript:void(0);)

```
1 /* Define NULL pointer value */
2 #ifndef NULL
3 #ifdef __cplusplus
4 #define NULL    0
5 #else  /* __cplusplus */
6 #define NULL    ((void *)0)
7 #endif  /* __cplusplus */
8 #endif  /* NULL */
```

[![复制代码](http://common.cnblogs.com/images/copycode.gif)](javascript:void(0);)

​    可以看出，在C中，NULL表示的是指向0的指针，而在C++中，NULL就直接跟0一样了。但有一点值得注意的是：在C语言中，“当常量0处于应该作为指针使用的上下文中时，它就作为空指针使用”（《征服C指针》）。例如，下边的指针定义和初始化是没问题的（即没警告也没报错）：

```
 int * p = 0;    /* C language */
```

​     但如果定义成如下的样子呢？

```
 int * p = 3;    /* C language */
```

　　很明显，这样子做是有问题的。这一句可以编译通过，但在VS 2013中有这样的警告：“warning C4047: “初始化”:“int *”与“int”的间接级别不同”。

　　我又试了一下这一句在C++中的情况，VS 2013就直接报错了：“ ‘int’ 类型的值不能用于初始化 ‘int *’ 类型的实体”。

　　因此，为了防止混淆，在C/C++中，当要将一个指针赋值为空指针的时候，都应该将它赋为NULL，而不是0。

　　

　　**2.** ‘\0’：‘\0’是一个“空字符”常量，它表示一个字符串的结束，它的ASCII码值为0。注意它与空格' '（ASCII码值为32）及'0'（ASCII码值为48）不一样的。

　　在《征服C指针》中，作者还提到了一种错误的程序写法：使用NULL来结束字符串。例如下边的程序就是有问题的：

```
char str[4] = { '1', '2', '3', NULL };    /* C language */
```

　　在VS 2013中，会的这样的警告：“warning C4047: “初始化”:“char”与“void *”的间接级别不同”。而在C++中，这一句是没有问题的。

　　还有一点值得注意，如下的程序在C/C++中都是没有问题的：

```
char str[4] = { '1', '2', '3', 0 };    / C/C++ language */
```

　　但为了防止混淆，在C/C++中，当要给一个字符串添加结束标志时，都应该用‘\0’而不是NULL或0。

 

　　综上所述，当我们要置一个指针为空时，应该用NULL，当我们要给一个字符串添加结束标志时，应该用‘\0’。

## 2.2 构造函数

使用 init 函数初始化对象存在的问题是，可能忘了初始化，可能初始化了多次。最好的办法就是显示调用一个函数，专门来做这个事，这个函数就是 constructor 

```c++
class Date {
	int d, m, y;
public:
	Date(int dd, int mm, int yy); // constructor
};

//两种构造
Date today = Date {23,6,1983};  //这种是不是有个临时对象？
Date xmas {25,12,1990}; // 上面的简写
Date xmas(25,12,1990); // 上面的简写
```

类对象如果有无参构造，会默认调用

```c++
Date::Date(){
	this->d = 1;
	this->m = 1;
	this->y = 1;
}

Date tt;
std::cout << tt.d << '\n' << tt.m << '\n' << tt.y << std::endl;
输出 3 个 1, 这是调用了默认的构造函数
```

默认参数值

```c++
//test.h
public:
	Date(int dd = 0, int mm = 1, int yy = 0);

//test.cpp
    Date::Date(int d, int m, int y){
	this->d = d;
	this->m = m;
	this->y = y;
}

//xxx.cpp
	Date tt{4};
	std::cout << tt.m << tt.yy << std::endl;
//输出 10 ，注意，实现的时候并没有把默认值带上，但是编译器可以识别，直接给了默认值
```

显示构造函数，explicit，这个关键字，使得可以不能构造函数，所谓隐式构造，如

```c++
//隐式构造
//test.h
public:
	Date(int, int, int);
//test.cpp
    Date::Date(int d, int m, int y){...}
//xxx.cpp
	Data day = {1, 2, 3}
```

这里是隐式构造，可以调试，可以确定调用了上述构造函数。但是如果使用关键字 

```c++
//test.h
public:
	explicit Date(int, int, int);
```

则这种无效，所以可以肯定的是， struct 的初始化列表是使用了隐式构造函数，下面来验证

```c++
//test.h
struct X{
	int a;
	explicit X();
	X(int a);
};
//test.cpp
X::X(int a){
	this->a = a;
}
X::X(){}

//xxxx.cpp
X y = {1}; 单参数构造函数，没有 explicit 修饰，可以隐式构造
X z;       这个不叫隐式构造，这个是显示调用
X s = {};  这个是真正的隐式构造，但是无参构造有 explicit 修饰，无法完成隐式构造。
```

## 2.3 copy move

传值，逻辑上两种不同选择:
• Copy is the conventional meaning of x=y; that is, the effect is that the values of x and y are
both equal to y’s value afterthe assignment.
• Move leaves x with y’s former value and y with some **moved-from state**. For the most interesting
cases, containers, that moved-from state is ‘‘empty.’’

### 2.3.1 copy

Copy for a class X is defined by two operations:
• Copy constructor: X(const X&)
• Copy assignment: X& operator=(const X&)



```c++
std::string fun_one(std::string vm)
{
	return vm;
}

std::string real = "real param";
std::string result = fun_one(real);
```

result 取得 "real param" 这个值，并不是那么显然。首先， 实参 real 拷贝临时 变量到 形参 vm 。在函数返回时，生成临时变量，把值再拷贝给 result 。这经过了两次拷贝，所以是很复杂的。下面分析一个例子

```c++
string ident(string arg){ return arg; }

int main ()
{
string s1 {"Adams"};
s1 = indet(s1);
string s2 {"Pratchett"};
s1 = s2;
}
```

这段代码所用到的函数如下:

• A constructor initializing a string with a string literal (构造了 s1 和 s2)
• A copy constructor copying a string (into the function argument arg)
• A move constructor moving the value of a string (from arg out of ident() into a temporary
variable holding the result of ident(s1))
• A move assignment moving the value of a string (from the temporary variable holding the
result of ident(s1) into s1)
• A copy assignment copying a string (from s2 into s1)
• A destructor releasing the resources owned by s1, s2, and the temporary variable holding the
result of ident(s1)

## 2.4 左值 右值

```c++
// 规则一：非const左值引用只能绑定到非const左值
A &lvalue_reference1 = lvalue;         // ok
A &lvalue_reference2 = const_lvalue;   // error
A &lvalue_reference3 = rvalue();       // error
A &lvalue_reference4 = const_rvalue(); // error

// 规则二：const左值引用可绑定到const左值、非const左值、const右值、非const右值
const A &const_lvalue_reference1 = lvalue;         // ok
const A &const_lvalue_reference2 = const_lvalue;   // ok
const A &const_lvalue_reference3 = rvalue();       // ok
const A &const_lvalue_reference4 = const_rvalue(); // ok

// 规则三：非const右值引用只能绑定到非const右值
A &&rvalue_reference1 = lvalue;         // error
A &&rvalue_reference2 = const_lvalue;   // error
A &&rvalue_reference3 = rvalue();       // ok
A &&rvalue_reference4 = const_rvalue(); // error

// 规则四：const右值引用可绑定到const右值和非const右值，不能绑定到左值
const A &&const_rvalue_reference1 = lvalue;         // error
const A &&const_rvalue_reference2 = const_lvalue;   // error
const A &&const_rvalue_reference3 = rvalue();       // ok
const A &&const_rvalue_reference4 = const_rvalue(); // ok

// 规则五：函数类型例外
void fun() {}
typedef decltype(fun) FUN;  // typedef void FUN();
FUN       &  lvalue_reference_to_fun       = fun; // ok
const FUN &  const_lvalue_reference_to_fun = fun; // ok
FUN       && rvalue_reference_to_fun       = fun; // ok
const FUN && const_rvalue_reference_to_fun = fun; // ok
```

## 2.5 重载（overloading）和重写（override）

重载，是同一个类的两个函数

override，是覆盖的意思，所以是子类与父类的关系

# 3 IO

分为字符文件读取和二进制文件读取

## 3.1 字符读取

| 区别   | fputc(char ch,FILE* fp)函数                           | fgetc(FILE* fp)函数                                   |
| ------ | ----------------------------------------------------- | ----------------------------------------------------- |
| 功能   | 将一个字符写入到文件中                                | 从文件中读出一个字符                                  |
| 参数   | ch要写入的字符,fp指向FILE结构的指针                   | ch要写入的字符,fp指向FILE结构的指针                   |
| 返回值 | 成功，返回该字符；遇到文件尾或读取错误时，返回EOF(-1) | 成功，返回该字符；遇到文件尾或读取错误时，返回EOF(-1) |

当按下回车键时，在标准输入缓存中，会多一个<font color=red>回车键</font>。windows系统在进行文件操作时，会将这个回车键转化为两个字符<font color=red>回车和换行</font>再写入文件中，而linux系统不会。读出时相反。

## 3.2 多字节读取

| 区别   | char *fgets(char *str, int num, FILE *fp)                    | int fputs(char *str, file *fp) |
| ------ | ------------------------------------------------------------ | ------------------------------ |
| 功能   | 读取指定数量字符，若数量限制为１０个，但是中途遇到换行符或者EOF，则返回，最多读9个字符，最后一个'\\0'是自动加的。文件存的是回车换行符，fgets并不会获取回车符，我觉得应该是系统自己把回车换行换成了换行符，再给fgets处理。 | 写入字符串                     |
| 参数   | str读到的字符存这里面，想要读取的字符个数，fp指向FILE结构的指针 | fp指向FILE结构的指针           |
| 返回值 | 成功，str指针。否则NULL                                      | 成功，返回0。否则非0           |

## 3.3 格式化输出到文件

```c
char * p = "abcdef";
double fl = 3.2;
int i = fprintf(stdout, "%5s\t%6.2f\n", p, fl);
```
## 3.4 fseek

```c
fseek(f, sizeof(int), SEEK_SET); 
```

读一个二进制文件，文件开始处偏移一个 int 型长度。

## 3.5 二进制文件

```c
FILE * f;
	int i;
	people per[3];
	char* p[] = { "li", "wang", "zhang" };
	per[0].age = 20; strcpy_s(per[0].name, strlen(p[0]) + 1, p[0]);

	per[1].age = 18; strcpy_s(per[1].name, strlen(p[1]) + 1, p[1]);

	per[2].age = 21; strcpy_s(per[2].name, strlen(p[2]) + 2, p[2]);

	errno_t err_t;
	if ((err_t = fopen_s(&f, "1.txt", "wb+")) != 0)
	{
		printf("cant open the file");
		exit(0);
	}

	for (i = 0; i < 3; i++)
	{
		if (fwrite(&per[i], sizeof(people), 1, f) != 1)
			printf("file write error\n");
	}

	fseek(f, 0L, SEEK_SET);

	people pp;
	while (fread(&pp, sizeof(people), 1, f) == 1){
		printf("%d %s\n", pp.age, pp.name);
	}

	system("pause");

	fclose(f);
```

输出

```c
20 li
18 wang
21 zhang
```



# 4 linux c/c++

## 4.2 makefile

### 4.2.1 gcc

```makefile
 1.预处理，生成预编译文件（.文件）：
        Gcc –E hello.c –o hello.i
    2.编译，生成汇编代码（.s文件）：
        Gcc –S hello.i –o hello.s
    3.汇编，生成目标文件（.o文件）：
        Gcc –c hello.s –o hello.o
    4.链接，生成可执行文件：
        Gcc hello.o –o hello
```

| 后缀名    | 所对应的语言                                                 |
| --------- | ------------------------------------------------------------ |
| -c        | 只是编译不链接，生成目标文件“.o”                             |
| -S        | 只是编译不汇编，生成汇编代码                                 |
| -E        | 只进行预编译，不做其他处理                                   |
| -g        | 在可执行程序中包含标准调试信息                               |
| -o file   | 把输出文件输出到file里                                       |
| -v        | 打印出编译器内部编译各过程的命令行信息和编译器的版本         |
| -I dir    | 在头文件的搜索路径列表中添加 dir 目录，不要包含文件名，是路径 |
| -L dir    | 在库文件的搜索路径列表中添加 dir 目录                        |
| -static   | 链接静态库                                                   |
| -llibrary | 连接名为library的库文件                                      |

### 4.2.1 没有头文件

```c
//count_words.c
#include <stdio.h>
#include <stdlib.h>

extern int fee_count, fie_count, foe_count, fum_count;
extern int yylex( void );
int main( int argc, char ** argv )
{
    yylex();
    printf( "%d %d %d %d\n", fee_count, fie_count, foe_count, fum_count );
    exit( 0 );
}
```

tab 键不能去掉，否则编译错误

```c
//lexer.l
	int fee_count = 0;
	int fie_count = 0;
	int foe_count = 0;
	int fum_count = 0;
%%
fee	fee_count++;
fie	fie_count++;
foe	foe_count++;
fum	fum_count++;
.
\n
```

命令用的tab键

```makefile
//makefile
count_words: count_words.o lexer.o -lfl
	gcc count_words.o lexer.o -lfl -o count_words
count_words.o: count_words.c
	gcc -c count_words.c
lexer.o: lexer.c
	gcc -c lexer.c
lexer.c: lexer.l
	flex -t lexer.l > lexer.c

```

运行结果:

```shell
./count_words << lexer.l
3 3 3 3 
```

### 4.2.2 有头文件

test 工程目录结构

>include
>
>>counter.h
>>
>>lexer.h
>
>src
>
>>count_words.c
>>
>>counter.c
>>
>>lexer.l
>
>makefile



如下是源代码：

```c
//lexer.h
#ifndef LEXER_H_
#define LEXER_H_
extern int fee_count, fie_count, foe_count, fum_count;
extern int yylex( void );
#endif

//lexer.l
	int fee_count = 0;
	int fie_count = 0;
	int foe_count = 0;
	int fum_count = 0;
%%
fee     fee_count++;
fie     fie_count++;
foe     foe_count++;
fum     fum_count++;
.
\n

//counter.h
#ifndef COUNTER_H_
#define COUNTER_H_
extern void counter(int args[4]);
#endif

//counter.c
#include <lexer.h>
#include <counter.h>
 
void counter( int counts[4]) {
        yylex();
        counts[0] = fee_count;
        counts[1] = fie_count;
        counts[2] = foe_count;
        counts[3] = fum_count;
}

//count_words.c
#include <stdio.h>
#include <lexer.h>
#include <counter.h>

int main( int argc, char ** argv ){
	int temp[4];
	int* p = temp;
	counter(p);
    printf( "%d %d %d %d\n", *p, *(p+1), *(p+2), *(p+3) );
    return( 0 );
}

//makefile
VPATH=src include
CC = gcc -g
CPPFLAGS = -I include
count_words: count_words.o counter.o lexer.o -lfl
	$(CC) $^ -o $@
count_words.o: count_words.c counter.h lexer.h
	$(CC) $(CPPFLAGS) -c $< -o $@ 
counter.o: counter.c counter.h lexer.h
	$(CC) $(CPPFLAGS) -c $< -o $@
lexer.o: lexer.c lexer.h
	$(CC) $(CPPFLAGS) -c $< -o $@
lexer.c: lexer.l
	flex -t $< > $@
.PHONY: clean
clean: 
	rm *.o lexer.c count_words
```

make

```shell
gcc -g -I include -c src/count_words.c -o count_words.o 
gcc -g -I include -c src/counter.c -o counter.o
flex -t src/lexer.l > lexer.c
gcc -g -I include -c lexer.c -o lexer.o
gcc -g count_words.o counter.o lexer.o /usr/lib/x86_64-linux-gnu/libfl.so -o count_words
```

makefile中  **VPATH** 可以换一种写法

```makefile
vpath %.l %.c src
vpath %.h include
```

### 4.2.3 VPATH

make file分析依赖关系的时候，要用到 vpath ，这个 vpath 事告诉make从何处找文件。

> Makefile
>
> src
>
> > main.c
> >
> > test.c
>
> include
>
> > test.h

这是告诉make工具，在分析依赖关系的时候，到上述两个目录去找。

```c
//main.c头文件依赖
include "stdio.h"
include "../include/test.h"
```

```makefile
vpath %.c src
vpath %.h include
CC=gcc

main:main.o test.o
main.o:main.c test.h
test.o:test.c

.PHONY:clean
clean:
	rm -rf *.o main
```

make工具，根据Makefile文件，会逐条分析依赖文件是否存在。makefile会将分析得到的源文件

路径传递给 gcc ，但是头文件不会传给 gcc ，这个可能的原因是 头文件是相对路径，不好控制。

```makefile
#make文件
vpath %.h include
vpath %.c src
CC=gcc

main:main.o test.o
main.o:main.c test.h
test.o:test.c

.PHONY:clean
clean:
	rm -rf *.o main

#make输出
gcc    -c -o main.o src/main.c
gcc    -c -o test.o src/test.c
gcc   main.o test.o   -o main
```

- vpath <directories>            :: 当前目录中找不到文件时, 就从<directories>中搜索
- vpath <pattern> <directories>  :: 符合<pattern>格式的文件, 就从<directories>中搜索
- vpath <pattern>                :: 清除符合<pattern>格式的文件搜索路径
- vpath                          :: 清除所有已经设置好的文件路径

### 4.2.4 include命令

这个命令可以包含其它的文件，共make使用，不是 gcc 命令

### 4.2.3 规则

% 代表 通配符（注意不是正则表达式）。一个工作目标以分为前后缀。-

- 静态模式规则

  ```makefile
  $(OBJECTS):%.o:%.c
      gcc -c $< -o $@
  ```

  \$(OBJECTS) 是目标文件列表，%.o 模式匹配 \$(OBJECTS) 中目标文件名，目标文件名替换进 %.c，得到必要条件。

- 后缀规则

  老系统才用后缀规则，新系统一般不用。

  ```makefile
  .c.o: 等价于 %.o:%.c
  ```

- 单后缀规则

  ```makefile
  .p: 等价于 %:%.p
  ```

- 扩展名列表

  ```makefile
  .SUFFIXES: .out .a .ln .c
  .SUFFIXES: .pdf .fo .html .xml
  ```

- 隐含规则

  GUN make 3.8  具有 90 多个隐含规则，涉及各种编程语言。隐含规则是上述规则的特殊情况。

  - \$* ：表示目标文件的名称，不包含目标文件的扩展名。

  - \$+ ：表示所有的依赖文件，这些依赖文件之间以空格分开，按照出现的先后为顺序，其中可能包含重复的依赖文件。

  - \$< ：表示依赖项中第一个依赖文件的名称

  - \$? ： 依赖项中，所有目标文件时间戳晚的文件（表示修改过），依赖文件间以空格分开

    \$@ ：目标项中目标文件的名称

  - \$^ ：依赖项中，所有不重复的依赖文件，以空格分开。

### 4.2.4 规则的结构

```makefile
%.o:%.c
    $(COMPILE.c) $(OUTPUT_OPTION) $<
COMPILE.c = $(CC) $(CFLAGS) $(CPPFLAGS) $(TARGET_ARCH) -C
CC = GCC
OUTPUT_OPTION = -O $@
```

CFLAGS : 编译选项

CPPFLAGS： 预编译选项

### 4.2.5 使用自定义makefile名字

make -f：-f 参数可以只从 makefile 文件的文件名字

## 4.3 一目标多规则

###4.3.1 例一

创建：

```shell
mkdir oneTarget
touch oneTarget/a.h
touch oneTarget/b.h
touch oneTarget/b.h
```

创建 Makefile 文件

```makefile
all:test
test:a.h
	@echo "this is a.h"
test:b.h
	@echo "this is b.h"
test:c.h
	@echo "this is c.h"
```

执行 make 命令，输出如下：

```shell
Makefile:5: warning: overriding recipe for target 'test'
Makefile:3: warning: ignoring old recipe for target 'test'
Makefile:7: warning: overriding recipe for target 'test'
Makefile:5: warning: ignoring old recipe for target 'test'
this is c.h
```

不管执行多少次，都是这个输出结果。原因是，分析 all  目标时发现依赖 test 所以要先执行 test 目标。这样 第 5 行和第 7 行 的脚本不能执行，原因时 target 重复，所以报 overriding 。分析完成后开始执行，从上往下分析，子下往上执行，先执行 第 9 行，输出 this is c.h ，第 5 行 和 第 3 行 由于 test 目标已执行，直接输出 ignoring。每次执行， test  都会被执行，原因时不存在 test 这个文件，就没法判断 test 的时间。

### 4.3.2 例二

在例一中，由于不产生 test 文件，导致每次都会执行 test 目标，现在修改 Makefile 文件，如下

```makefile
all:test
test:a.h
	@echo "this is a.h"
test:b.h
	@echo "this is b.h"
test:c.h
	@echo "this is c.h"
	touch test
```

首次执行 make，输出

```shell
Makefile:5: warning: overriding recipe for target 'test'
Makefile:3: warning: ignoring old recipe for target 'test'
Makefile:7: warning: overriding recipe for target 'test'
Makefile:5: warning: ignoring old recipe for target 'test'
this is c.h
touch test
```

生成了 test 文件，第二次执行 make 输出

```shell
Makefile:5: warning: overriding recipe for target 'test'
Makefile:3: warning: ignoring old recipe for target 'test'
Makefile:7: warning: overriding recipe for target 'test'
Makefile:5: warning: ignoring old recipe for target 'test'
make: Nothing to be done for 'all'.
```

第二次什么都没做，原因是 第一次生成的 test 文件是最新的。接下来我们更新 a.h 文件

```shell
echo "aaaa" > a.h
```

由于 a.h 的更新，导致 test 目标被执行，为什么不输出  "this is a.h" 呢，原因是 make 只能判断出 test 目标要更新，但更新规则还是自下而上的。

引入 file.txt 文件，修改 Makefile 文件。 file.txt  文件如下

```makefile
test:a.h
	@echo "this is a.h"
```

Makefile 文件如下

```makefile
all:test

include file.txt
test:b.h
	@echo "this is b.h"
test:c.h
	@echo "this is c.h"
	touch test
```

执行 make ，输出 类似与 例二。这里想说明的是， include 指令成功地将 file.txt 中的指令包含到 Makefile文件中了。

### 4.3.3 例三

下面我们继续对Makefile文件和file.txt文件进行修改，并新建d.h文件；

修改后的 file.txt 文件和Makefile文件的内容分别如下：

```makefile
file.txt:a.h
```

Makefile 文件

```makefile
all:test
include file.txt
file.txt:b.h
	@echo "test: d.h" > file.txt 
test:c.h
	@echo "this is c.h"
	touch test
```

我们依次输入以下命令：

```shell
$ls
a.h  b.h  c.h  d.h  file.txt  Makefile  test
$touch b.h
$touch d.h
$make
this is c.h
touch test
```

查看 file.txt 内容，发现内容变为

```makefile
test.txt:d.h
```

分析原因，include 命令包含了`file.txt`文件，故 include 命令会检查以`file.txt`为目标的全部规则。由于 `b.h` 比`file.txt`新，导致 `file.txt:b.h` 这条规则起作用，使得`file.txt`内容被更新。include 会将更新了的`file.txt`文件重新加到`Makefile`文件中，最终包含到 `Makefile`中的内容是`test.txt:d.h`，include 命令执行完毕。接着执行 all 目标的规则，由于更新了`d.h`，所以 test 目标要被更新，根据自下而上的执行规则，

```makefile
	test:c.h
	@echo "this is c.h"
	touch test
```

这条规则要被执行，输出如上。

## 4.4 注释

下面四种注释中，第二种是通过命令是现实得

```makefile
#顶格写注释，make不显示
	echo    #linux输出注释
	#这个注释在make时显示
	@#这个注释不显示
```

## 4.5 解决依赖问题

### 4.5.1 include

生成依赖文件 depend ，然后将 depend 文件 include 进 makefile 中

```makefile
depend: main.c test.c
	gcc -M $^ > depend
include depend
```

上述 makefile 命令产生的  depend 文件如下：

```makefile
main.o: main.c /usr/include/stdc-predef.h /usr/include/stdio.h \
 /usr/include/features.h /usr/include/x86_64-linux-gnu/sys/cdefs.h \
 /usr/include/x86_64-linux-gnu/bits/wordsize.h \
 /usr/include/x86_64-linux-gnu/gnu/stubs.h \
 /usr/include/x86_64-linux-gnu/gnu/stubs-64.h \
 /usr/lib/gcc/x86_64-linux-gnu/4.9/include/stddef.h \
 /usr/include/x86_64-linux-gnu/bits/types.h \
 /usr/include/x86_64-linux-gnu/bits/typesizes.h /usr/include/libio.h \
 /usr/include/_G_config.h /usr/include/wchar.h \
 /usr/lib/gcc/x86_64-linux-gnu/4.9/include/stdarg.h \
 /usr/include/x86_64-linux-gnu/bits/stdio_lim.h \
 /usr/include/x86_64-linux-gnu/bits/sys_errlist.h test.h
test.o: test.c /usr/include/stdc-predef.h
```

由于我们使用了 `include depend` 语句，这个文件的内容会自动加入到 makefile 中。但是这样做存在问题，我们的目标必要条件是 `main.c test.c` ，想要在必要条件中新增一条或者删除一条必要条件，depend 不会重新生成，这导致不能增加源文件数量。

### 4.5.2 sed替换

先打印几个函数看看

```makefile
#获取 c 文件列表
sources:=$(wildcard *.c)  #wildcard函数
objects:=$(sources:.c=.o) #$(: =) 函数
target:=$(sources:.c=)
#这里,dependence是所有.d文件的列表.即把串sources串里的.c换成.d  
dependence:=$(sources:.c=.d)

.PHONY:study
study:
	@for s in $(sources); \
                 do echo "sources are $$s"; done;
	@for ss in $(objects); \
                 do \
	             echo "objects are $$ss"; \  #$$ss拼出$ss
	             done
	@for sss in $(target); \
	             do \
	             echo "target are $$sss"; \
	             done;
```

能够运行的 main

```makefile
#获取 c 文件列表，给出 objects 和 dependence文件
sources:=$(wildcard *.c)
objects:=$(sources:.c=.o)
dependence:=$(sources:.c=.d)

main:main.o test.o
	gcc -o main main.o test.o

include $(dependence)
%.d: %.c
	set -e; rm -f $@; \
	gcc -M $< > $@.$$$$; \
	sed 's,\($*\)\.o[ :]*,\1.o $@ : ,g' < $@.$$$$ > $@; \
	rm -f $@.$$$$;

.PHONY: clean
clean:
	rm -f main $(objects) $(dependence);
```

下面分析这段脚本

由于有 <font color=red>include</font> 关键字，系统先分析  <font color=red>include</font> 这句， <font color=red>dependence</font> 的值是 *.d ，这个文件不存在，makefile 发现规则，这个规则作用于 *.d 文件，故先这执行这个规则，这个规则在任何时候都是最先执行的。

```makefile
%.d: %.c
	set -e; rm -f $@; \
	gcc -M $< > $@.$$$$; \
	sed 's,\($*\)\.o[ :]*,\1.o $@ : ,g' < $@.$$$$ > $@; \
	rm -f $@.$$$$;
```

第一行，set -e 作用是，规则报错时返回。 rm -f 删除旧的 *.d 文件。 第二条命令将 *.c 的依赖放入相应的 *.d.xxx 临时文件中。第三条命令

```makefile
sed 's,\($*\)\.o[ :]*,\1.o $@ : ,g' < $@.$$$$ > $@; 
```

是对*.d.xxx 临时文件内容进行处理。分成四个部分

```makefile
sed 's,\($*\)\.o[ :]*,\1.o $@ : ,g' < $@.$$$$ > $@; 
s
# 这是 vim 字符串替换的标识符
\($*\)\.o[ :]*
# 如main.d: %main.c 首先\($*\)捕捉 main ，和后面的 \.o 组合成 mian.o。[ :]可以空格或者冒号，后面的*号是无限匹配，可以匹配出多个空格和冒号。实际文件中只有main.o :，也只匹配出这个。 \.o 据说 \是多余的。
\1.o $@ : 
# 对于前面捕捉到的main.o : ，进行替换，\1 是前面\($*\)捕捉的 main，合在一起就是main.o main.d:，说白了，就是用 main.o main.d 替换 main.o 
g 
# 这是 vim 字符串替换的标识符
```

第四条命令删除临时生成的 *.d.xxx文件。

```makefile
main:main.o test.o
	gcc -o main main.o test.o
```

这个规则执行后，发现缺少 .o 文件，会根据第三条命令生成的规则，生成 .o 文件，最终生成可执行文件

## 4.6 宏

多条命令，使用宏来做。test是一个宏

```makefile
define test 
@echo 这是第一行
@echo 这是第二行
endef

.PHONY:all
all:
	$(test)
```

输出

```shell
wudeyun@www:~/aaa$ make
这是第一行
这是第二行
```

## 4.7 内置函数

### 4.7.1 filter 收集匹配的

将words看成一个个单词，来匹配

```makefile
words := he the hen other the%

.PHONY:all
all:
	@echo he matches: $(filter he, $(words))
	@echo %he matches: $(filter %he, $(words))
	@echo he% matches: $(filter he%, $(words))
	@echo %he% matches: $(filter %he%, $(words))
```

输出

```shell
wudeyun@www:~/aaa$ make
he matches: he
%he matches: he the
he% matches: he hen
%he% matches: the%
```

### 4.7.2 filter-out 收集不匹配的

```makefile
words := he the hen other the%

.PHONY:all
all:
	@echo he matches: $(filter-out he, $(words))
	@echo %he matches: $(filter-out %he, $(words))
	@echo he% matches: $(filter-out he%, $(words))
	@echo %he% matches: $(filter-out %he%, $(words))
```

输出

```shell
wudeyun@www:~/aaa$ make
he matches: the hen other the%
%he matches: hen other the%
he% matches: the other the%
%he% matches: he the hen other
```

### 4.7.3 findstring 查早字串

```makefile
.PHONY:all
all:
	@echo find path: $(findstring xx, aa bb xx)
```

### 4.7.4 subst 字节替换

```makefile
sources := 1.c 2.c 3.c 4.c
objects := $(subst .c,.o, $(sources))
.PHONY:all
all:
	@for i in $(objects);\
	do \
         echo "$$i";\
	done
```

输出

```makefile
wudeyun@www:~/aaa$ make
1.o
2.o
3.o
4.o
```

### 4.7.5 patsubst 模式匹配替换

```makefile
sources := /root/a /root/b
objects := $(patsubst /%,%, $(sources))
.PHONY:all
all:
	@for i in $(objects);\
	do \
         echo "$$i";\
	done
```

输出

```makefile
wudeyun@www:~/aaa$ make
root/a
root/b
```

### 4.7.6 words单词数量

###4.7.7 firstword

###4.7.8 wordlist

### 4.7.9 wildcard

```makefile
$(wildcard *.c *.h) #获取.c 和 .h文件
```

### 4.7.10 dir 返回目录部分

```makefile
$(dir list)
```

返回目录部分

###4.7.11 notdir返回名字部分

$(notdir list)

### 4.7.12 suffix后缀

```makefile
$(suffix list)
```

### 4.7.13 basename去掉后缀

```makefile
$(basename list)
```

### 4.7.14 增加后缀

```makefile
$(addsuffix suffix,name)
```

###4.7.15 foreach

```makefile
names := a b c d
files := $(foreach n,$(names),$(n).o)
.PHONY:all
all:
	@for i in $(files);\
	do \
         echo "$$i";\
	done
```

输出

```makefile
$(name)中的单词会被挨个取出，并存到变量“n”中，“$(n).o”每次根据“$(n)”计算出一个值，这些值以空格分隔，最后作为foreach函数的返回，所以，$(files)的值是“a.o b.o c.o d.o”。
```

### 4.7.16 if

（if 条件，else, then）

```makefile
names := a b c d
names += $(if $((2>1)),2,3)
.PHONY:all
all:
	@for i in $(names);\
	do \
         echo "$$i";\
	done
```

输出

```shell
a
b
c
d
2
```

## 4.8 自定义函数

```makefile
.PHONY : test
 
define foo1
    @echo "My name is $(0)"
endef
 
define foo2
    @echo "my name is $(0)"
    @echo "param => $(1)"
endef
 
var := $(call foo1)    
new := $(foo1)         
 
test :
    @echo "var => $(var)"   
    @echo "new => $(new)"   
    $(call foo1) 
    $(call foo2, wudeyun)
```

输出

```shell
wudeyun@www:~/aaa$ make
My name is foo1
my name is foo2
param =>  wudeyun
```

## 4.9 静态库

ar命令可以用来创建、修改库，也可以从库中提出单个模块。库是一单独的文件，里面包含了按照特定的结构组织起来的其它的一些文件（称做此库文件的member）。原始文件的内容、模式、时间戳、属主、组等属性都保留在库文件中。
　　下面是ar命令的格式：
　　ar [-]{dmpqrtx}[abcfilNoPsSuvV][membername] [count] archive files...
　　例如我们可以用**ar rv libtest.a hello.o hello1.o**来
生成一个库，库名字是test，链接时可以用-ltest链接。该库中存放了两个模块hello.o和hello1.o。选项前可以有‘-'字符，也可以
没有。下面我们来看看命令的操作选项和任选项。现在我们把{dmpqrtx}部分称为操作选项，而[abcfilNoPsSuvV]部分称为任选项。
　　{dmpqrtx}中的操作选项在命令中只能并且必须使用其中一个，它们的含义如下：

- d：从库中删除模块。按模块原来的文件名指定要删除的模块。如果使用了任选项v则列出被删除的每个模块。
- m：该操作是在一个库中移动成员。当库中如果有若干模块有相同的符号定义(如函数定义)，则成员的位置顺序很重要。如果没有指定任选项，任何指定的成员将移到库的最后。也可以使用'a'，'b'，或'i'任选项移动到指定的位置。
- p：显示库中指定的成员到标准输出。如果指定任选项v，则在输出成员的内容前，将显示成员的名字。如果没有指定成员的名字，所有库中的文件将显示出来。
- q：快速追加。增加新模块到库的结尾处。并不检查是否需要替换。'a'，'b'，或'i'任选项对此操作没有影响，模块总是追加的库的结尾处。如果使用了任选项v则列出每个模块。 这时，库的符号表没有更新，可以用'ar s'或ranlib来更新库的符号表索引。
- r：在库中插入模块(替换)。当插入的模块名已经在库中存在，则替换同名的模块。如果若干模块中有一个模块在库中不存在，ar显示一个错误消息，并不替换其他同名模块。默认的情况下，新的成员增加在库的结尾处，可以使用其他任选项来改变增加的位置。
- t：显示库的模块表清单。一般只显示模块名。
- x：从库中提取一个成员。如果不指定要提取的模块，则提取库中所有的模块。


　　下面在看看可与操作选项结合使用的任选项：

- a：在库的一个已经存在的成员后面增加一个新的文件。如果使用任选项a，则应该为命令行中membername参数指定一个已经存在的成员名。
- b：在库的一个已经存在的成员前面增加一个新的文件。如果使用任选项b，则应该为命令行中membername参数指定一个已经存在的成员名。
- c：创建一个库。不管库是否存在，都将创建。
- f：在库中截短指定的名字。缺省情况下，文件名的长度是不受限制的，可以使用此参数将文件名截短，以保证与其它系统的兼容。
- i：在库的一个已经存在的成员前面增加一个新的文件。如果使用任选项i，则应该为命令行中membername参数指定一个已经存在的成员名(类似任选项b)。
- l：暂未使用
- N：与count参数一起使用，在库中有多个相同的文件名时指定提取或输出的个数。
- o：当提取成员时，保留成员的原始数据。如果不指定该任选项，则提取出的模块的时间将标为提取出的时间。
- P：进行文件名匹配时使用全路径名。ar在创建库时不能使用全路径名（这样的库文件不符合POSIX标准），但是有些工具可以。
- s：写入一个目标文件索引到库中，或者更新一个存在的目标文件索引。甚至对于没有任何变化的库也作该动作。对一个库做ar s等同于对该库做ranlib。
- S：不创建目标文件索引，这在创建较大的库时能加快时间。
- u：一般说来，命令ar r...插入所有列出的文件到库中，如果你只想插入列出文件中那些比库中同名文件新的文件，就可以使用该任选项。该任选项只用于r操作选项。
- v：该选项用来显示执行操作选项的附加信息。
- V：显示ar的版本。

## 4. 10 动态库

Linux下动态库文件的文件名形如 `libxxx.so`，其中so是 Shared Object 的缩写，即可以共享的目标文件。

在链接动态库生成可执行文件时，并不会把动态库的代码复制到执行文件中，而是在执行文件中记录对动态库的引用。

程序执行时，再去加载动态库文件。如果动态库已经加载，则不必重复加载，从而能节省内存空间。

Linux下生成和使用动态库的步骤如下：

1. 编写源文件。
2. 将一个或几个源文件编译链接，生成共享库。
3. 通过 `-L<path> -lxxx` 的gcc选项链接生成的libxxx.so。
4. 把libxxx.so放入链接库的标准路径，或指定 `LD_LIBRARY_PATH`，才能运行链接了libxxx.so的程序。

下面通过实例详细讲解。

## 编写源文件

建立一个源文件： max.c，代码如下：

```
int max(int n1, int n2, int n3)
{
    int max_num = n1;
    max_num = max_num < n2? n2: max_num;
    max_num = max_num < n3? n3: max_num;
    return max_num;
}
```

编译生成共享库：

```
gcc -fPIC -shared -o libmax.so max.c
```

我们会得到libmax.so。

实际上上述过程分为编译和链接两步， -fPIC是编译选项，PIC是 Position Independent Code 的缩写，表示要生成位置无关的代码，这是动态库需要的特性； -shared是链接选项，告诉gcc生成动态库而不是可执行文件。

上述的一行命令等同于：

```
gcc -c -fPIC max.c
gcc -shared -o libmax.so max.o
```

## 为动态库编写接口文件

为了让用户知道我们的动态库中有哪些接口可用，我们需要编写对应的头文件。

建立 max.h ，输入以下代码：

```
#ifndef __MAX_H__
#define __MAX_H__

int max(int n1, int n2, int n3);

#endif
```

## 测试，链接动态库生成可执行文件

建立一个使用`max`函数的test.c，代码如下：

```
#include <stdio.h>
#include "max.h"

int main(int argc, char *argv[])
{
    int a = 10, b = -2, c = 100;
    printf("max among 10, -2 and 100 is %d.\n", max(a, b, c));
    return 0;
}
```

`gcc test.c -L. -lmax` 生成a.out，其中`-lmax`表示要链接`libmax.so`。
`-L.`表示搜索要链接的库文件时包含当前路径。

注意，如果同一目录下同时存在同名的动态库和静态库，比如 `libmax.so` 和 `libmax.a` 都在当前路径下，
则gcc会优先链接动态库。

## 运行

运行 `./a.out` 会得到以下的错误提示。

```
./a.out: error while loading shared libraries: libmax.so: cannot open shared object file: No such file or directory
```

找不到libmax.so，原来Linux是通过 `/etc/ld.so.cache` 文件搜寻要链接的动态库的。
而 `/etc/ld.so.cache` 是 ldconfig 程序读取 `/etc/ld.so.conf` 文件生成的。
（注意， `/etc/ld.so.conf` 中并不必包含 `/lib` 和 `/usr/lib`，`ldconfig`程序会自动搜索这两个目录）

如果我们把 `libmax.so` 所在的路径添加到 `/etc/ld.so.conf` 中，再以root权限运行 `ldconfig` 程序，更新 `/etc/ld.so.cache` ，`a.out`运行时，就可以找到 `libmax.so`。

但作为一个简单的测试例子，让我们改动系统的东西，似乎不太合适。
还有另一种简单的方法，就是为`a.out`指定 `LD_LIBRARY_PATH`。

```
LD_LIBRARY_PATH=. ./a.out
```

程序就能正常运行了。`LD_LIBRARY_PATH=.` 是告诉 `a.out`，先在当前路径寻找链接的动态库。

> 对于elf格式的可执行程序，是由ld-linux.so*来完成的，它先后搜索elf文件的 `DT_RPATH` 段, 环境变量 `LD_LIBRARY_PATH`, /etc/ld.so.cache文件列表, /lib/,/usr/lib目录, 找到库文件后将其载入内存. (http://blog.chinaunix.net/uid-23592843-id-223539.html)

## makefile让工作自动化

编写makefile，内容如下：

```
.PHONY: build test clean

build: libmax.so

libmax.so: max.o
    gcc -o $@  -shared $<

max.o: max.c
    gcc -c -fPIC $<

test: a.out

a.out: test.c libmax.so
    gcc test.c -L. -lmax
    LD_LIBRARY_PATH=. ./a.out

clean:
    rm -f *.o *.so a.out
```

`make build`就会生成`libmax.so`， `make test`就会生成`a.out`并执行，`make clean`会清理编译和测试结果。

# 5 进程环境

## 5.1 C 程序起始地址

C 程序在链接生成可执行文件时，连接器会把一个启动例程链接到程序中，作为程序的起始地址，启动例程先获取命令行参数和环境变量值，然后调用main方法。C 程序总是从 main 方法开始执行。

##5.2 线程终止

- 从main返回
- 调用 exit
- 调用 _exit 和_Exit
- 最后一个线程，从其启动例程返回
- 最后一个线程调用 pThread_exit

异常终止三种

- abort
- 收到信号
- 最后一个线程对取消做出响应

## 5.3 中止处理函数

可以使用atexit登记多达 32 个中止处理函数。启动例程一般用<font color=red>汇编语言</font>编写，在main函数返回后执行。用 C 代码的形式是 exit( main(argc, argv) )，exit 会自动调用终止处理程序。

```c
#include <stdio.h>
#include <stdlib.h>

static void my_exit1();
static void my_exit2();

int main()
{
	if (atexit(my_exit1) != 0){
		printf("can't register my_exit1!");
	}

	if (atexit(my_exit2) != 0){
		printf("can't register my_exit2!");
	}

	printf("main is done!\n");
	return 0;
}

static void my_exit1(){
	printf("first exit handler\n");
}

static void my_exit2(){
	printf("second exit handler\n");
}
```

## 5.4 命令行参数

```c
#include "stdio.h"
#include "stdlib.h"

int  main(int argc, char* argv[]){
    int i;
    for(i = 0; NULL != argv[i]; ++i){
        printf("argv[%d]: %s\n", i, argv[i]);
    }
    exit(0);
}
```

ISO  C 和UNIX 都规定， argv[argc] = null，就是多加了一个null在数组后边

```c
#include <stdio.h>

int main(int argc, char* argv[])
{
	for (int i = 0; i < argc; ++i){
		printf("%s\n", argv[i]);
	}
	return 0;
}
```

输出，显然程序名也是一个参数

```
E:\work\VS_SPACE\project1\solution_study1\Debug>c1project.exe first second
c1project.exe
first
second
```



## 5.5 环境表

```c
#include "stdio.h"
#include "stdlib.h"
extern char** environ;

int  main(int argc, char* argv[]){
    if(NULL != environ){
        while(NULL != *environ){
	    printf("%s\n", (*environ++));
	} 
    }
}
```

每个程序都有一张环境表，char** environ 是一个全局变量，getenv 和 putevn操作特定的变量。

# 6 进程控制

## 6.1 fork

复制线程，环境不同

## 6.2 vfork()

创建线程，单环境共用。可以保证子线程先运行，直到子线程调用 exec 或者  exit 之后，父进程才能执行，在此之前，子进程域父进程共用一个空间。 

# 7 信号

