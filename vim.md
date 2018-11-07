# 1 插件

- [Vundle](https://github.com/wutongtongshu/Vundle.vim) 管理插件
  - PluginInstall 安装插件
  - PluginList 插件列表
  - PluginClean清除插件

- majutsushi/tagbar 跳转插件
- kien/ctrlp.vim 搜索插件
- scrooloose/nerdtree 目录树插件
- Valloric/YouCompleteMe自动补齐插件

# 2 映射配置

```vim
map <F10> <Esc>:tabnew<CR>
```

<Esc>代表Escape键；<CR>代表Enter键。相当于先按Escape，再输入":" 再输入tabnew，最后按Enter



有五種映射存在 
- 用於普通模式: 輸入命令時。 
- 用於可視模式: 可視區域高亮並輸入命令時。 
- 用於操作符等待模式: 操作符等待中 ("d"，"y"，"c" 等等之後)。 
見下: |omap-info|。 
- 用於插入模式: 也用於替換模式。 
? 用於命令行模式: 輸入 ":" 或 "/" 命令時。 

下表是map綁定中，對應的模式代號。現在先了解一下，等看完之後再回過頭看這個模式代號就會明白了。 
字 符 模 式 ~ 
<Space> 普通、可視、選擇和操作符等待 
n 普通 
v 可視和選擇 
s 選擇 (在可視模式下Ctrl+G進入)
x 可視 
o 操作符等待 
! 插入和命令行 
i 插入 
l 插入、命令行和 Lang-Arg 模式的 ":lmap" 映射 
c 命令行 

我主要講解一下「n(普通模式)」下的兩個綁定命令，等看完之後就對應的明白別的模式下的命令了。 
適用於普通模式的映射命令主要有： 
\1. :map 
[語法] :map {lhs} {rhs} |mapmode-nvo| *:map* 
1.1 作用模式： n、v、o （普通、可視和選擇、操作符等待） 
1.2 命令格式： 
:map {lhs} {rhs} 
含義： 在:map作用的模式中把鍵系列 {lhs} 映射為 {rhs}，{rhs}可進行映射掃描，也就是可遞歸映射。 
1.3 舉例： 
:map td :tabnew .<cr> 
含義：在其作用模式（普通、可視、操作符）下，輸入td等價於輸入 :tabnew . <cr>。而普通模式下輸入:tabnew . <cr>就是打開當前目錄 
如果再定義綁定 :map ts td，就是指在其作用模式下輸入ts等價於td，也就是打開當前目錄。不過如果沒有特殊需要，一般不建議遞歸映射。 

\2. :noremap 
:moremap和:map命令相對，作用模式和命令格式都相同，只不過不允許再對{rhs}進行映射掃描，也就是{lhs}定義後的映射就是{rhs}的鍵序列，不會再對{rhs}鍵序列重新解釋掃描。它一般用於重定義一個命令，當然如果:map不需要遞歸映射的話，建議試用:noremap 
比如： 
:noremap ts td 
它的意思是在其作用模式下，輸入ts就是輸入td，但是和:map不同的是，此時td再不會做進一步掃描解釋。雖然之前已經定義了td，但是不會對td再做掃描 

\3. :unmap 
:unmap是對應取消:map綁定的｛lhs｝，作用模式相同，命令格式 :unmap {lhs}。 
例如： 
:unmap td 
就是取消在其作用模式中td的綁定，比如之前td被綁定為:tabnew .<cr>，此時此綁定消失。 
\4. :mapclear 
:mapclear時對應取消所有:map綁定的，慎用！ 

\5. :nmap 
:nmap是:map的普通模式板，也就是說其綁定的鍵只作用於普通模式。 
例如： 
:nmap td :tabnew .<cr> 和 :map td :tabnew .<cr> 在普通模式下等效 
\6. :nnoremap 
:nnorempa和:nmap的關係和:noremap和:map的關係一樣，只是:nmap的非遞歸版 
\7. :nunmap 
:nunmap和:nmap的關係和:unmap和:map的關係一樣，取消:nmap的綁定。 
\8. :nmapclear 
:nmapclear是對應取消所有:map綁定的，慎用！ 

看完以上，應該可以發現一個規律，前4個是一組，後4個時一組，後一組比前一組多一個n就是指只作用於普通模式。其中每組內*nore*是其對應的非遞歸版、*un*是取消綁定某個<lhs>綁定、clear後綴是取消所有綁定。發現了這個規律，再翻到前面的模式代號表，你大體可以猜到vmap、xmap、smap、omap是什麼意思了吧，以及相對應的nore版本、un版本、clear版本。 

另外： 
{rhs} 之前可能顯示一個特殊字元: 
\* 表示它不可重映射 
& 表示僅腳本的局部映射可以被重映射 
@ 表示緩衝區的局部映射 

到這一步你可以輕鬆的長吸一口氣，因為相關的命令已經都了解了，記不住沒關係，可以隨時:help map一下。不過別急，後面還有map更多的選項等著去攻克。 

鍵表 |key-notation| 
<k0> - <k9> 小鍵盤 0 到 9 *keypad-0* *keypad-9* 
<S-...> Shift＋鍵 *shift* *<S-* 
<C-...> Control＋鍵 *control* *ctrl* *<C-* 
<M-...> Alt＋鍵 或 meta＋鍵 *meta* *alt* *<M-* 
<A-...> 同 <m-...> *<A-* 
<t_xx> termcap 里的 "xx" 入口鍵 

特殊參數： 
1. <buffer> 
2. <silent> 
3. <special> 
4. <script> 
5. <expr> 
6. <unique> 
它們必須映射命令的後邊，在其他任何參數的前面。 

<buffer>如果這些映射命令的第一個參數是<buffer>，映射將只局限於當前緩衝區（也就是你此時正編輯的文件）內。比如： 
:map <buffer> ,w /a<CR> 
它的意思時在當前緩衝區里定義鍵綁定，「,w」將在當前緩衝區里查找字元a。同樣你可以在其他緩衝區里定義： 
:map <buffer> ,w /b<CR> 
比如我經常打開多個標籤(:tabedit)，想要在各自標籤里定義",w"鍵綁定，那麼你只要在每個標籤頁里分別定義就可，其作用域也只在各自的標籤里。同樣要清除這些緩衝區的鍵綁定也要加上<buffer>參數，比如： 
:unmap <buffer> ,w 
:mapclear <buffer> 

<silent>是指執行鍵綁定時不在命令行上回顯，比如： 
:map <silent> ,w /abcd<CR> 
你在輸入,w查找abcd時，命令行上不會顯示/abcd，如果沒有<silent>參數就會顯示出來 

<special>一般用於定義特殊鍵怕有副作用的場合。比如： 
:map <special> <F12> /Header<CR> 

<unique>一般用於定義新的鍵映射或者縮寫命令的同時檢查是否該鍵已經被映射，如果該映射或者縮寫已經存在，則該命令會失敗 

<expr>. 如果定義新映射的第一個參數是<expr>，那麼參數會作為表達式來進行計算，結果使用實際使用的<rhs>，例如： 
:inoremap <expr> . InsertDot() 
這可以用來檢查游標之前的文本並在一定條件下啟動全能 (omni) 補全。 
一個例子： 
let counter = 0 
inoremap <expr> <C-L> ListItem() 
inoremap <expr> <C-R> ListReset() 

func ListItem() 
let g:counter += 1 
return g:counter . '. ' 
endfunc 

func ListReset() 
let g:counter = 0 
return '' 
endfunc 
在插入模式下，CTRL-L插入順序的列表編號，並返回；CTRL-R複位列表編號到0，並返回空。 

<Leader> mapleader 
mapleader變數對所有map映射命令起效，它的作用是將參數<leader>替換成mapleader變數的值，比如： 
:map <Leader>A oanother line<Esc> 
如果mapleader變數沒有設置，則用默認的反斜杠代替，因此這個映射等效於： 
:map \A oanother line<Esc> 
意思時輸入\A鍵時，在下一行輸入another line並返回到普通模式。 
如果設置了mapleader變數，比如： 
let mapleader = "," 
那麼就等效於： 
:map ,A oanother line<Esc> 

<LocalLeader> maplocalleader 
<LocalLeader>和<Leader>類似，只不過它只作用於緩衝區。 
因此在設置mapleader和maplocalleader時最好區分開，不要出現衝突。 

大體上映射的主要部分已經都提到了，還有很多具體的映射相關的內容可以參見:help map

# 3 编码

Vim 有四个跟字符编码方式有关的选项，encoding、fileencoding、fileencodings、termencoding (这些选项可能的取值请参考 Vim 在线帮助 :help encoding-names)，它们的意义如下:

\* encoding: Vim 内部使用的字符编码方式，包括 Vim 的 buffer (缓冲区)、菜单文本、消息文本等。默认是根据你的locale选择.用户手册上建议只在 .vimrc 中改变它的值，事实上似乎也只有在.vimrc 中改变它的值才有意义。你可以用另外一种编码来编辑和保存文件，如你的vim的encoding为utf-8,所编辑的文件采用cp936编码,vim会 自动将读入的文件转成utf-8(vim的能读懂的方式），而当你写入文件时,又会自动转回成cp936（文件的保存编码).

\* fileencoding: Vim 中当前编辑的文件的字符编码方式，Vim 保存文件时也会将文件保存为这种字符编码方式 (不管是否新文件都如此)。

\* fileencodings: Vim自动探测fileencoding的顺序列表，启动时会按照它所列出的字符编码方式逐一探测即将打开的文件的字符编码方式，并且将 fileencoding 设置为最终探测到的字符编码方式。因此最好将Unicode 编码方式放到这个列表的最前面，将拉丁语系编码方式 latin1 放到最后面。

\* termencoding: Vim 所工作的终端 (或者 Windows 的 Console 窗口) 的字符编码方式。如果vim所在的term与vim编码相同，则无需设置。如其不然，你可以用vim的termencoding选项将自动转换成term 的编码.这个选项在 Windows 下对我们常用的 GUI 模式的 gVim 无效，而对 Console 模式的Vim 而言就是 Windows 控制台的代码页，并且通常我们不需要改变它。

好了，解释完了这一堆容易让新手犯糊涂的参数，我们来看看 Vim 的多字符编码方式支持是如何工作的。

\1. Vim 启动，根据 .vimrc 中设置的 encoding 的值来设置 buffer、菜单文本、消息文的字符编码方式。

\2. 读取需要编辑的文件，根据 fileencodings 中列出的字符编码方式逐一探测该文件编码方式。并设置 fileencoding 为探测到的，看起来是正确的 (注1) 字符编码方式。

\3. 对比 fileencoding 和 encoding 的值，若不同则调用 iconv 将文件内容转换为encoding 所描述的字符编码方式，并且把转换后的内容放到为此文件开辟的 buffer 里，此时我们就可以开始编辑这个文件了。注意，完成这一步动作需要调用外部的 iconv.dll(注2)，你需要保证这个文件存在于 $VIMRUNTIME 或者其他列在 PATH 环境变量中的目录里。

\4. 编辑完成后保存文件时，再次对比 fileencoding 和 encoding 的值。若不同，再次调用 iconv 将即将保存的 buffer 中的文本转换为 fileencoding 所描述的字符编码方式，并保存到指定的文件中。同样，这需要调用 iconv.dll由于 Unicode 能够包含几乎所有的语言的字符，而且 Unicode 的 UTF-8 编码方式又是非常具有性价比的编码方式 (空间消耗比 UCS-2 小)，因此建议 encoding 的值设置为utf-8。这么做的另一个理由是 encoding 设置为 utf-8 时，Vim 自动探测文件的编码方式会更准确 (或许这个理由才是主要的 ;)。我们在中文 Windows 里编辑的文件，为了兼顾与其他软件的兼容性，文件编码还是设置为 GB2312/GBK 比较合适，因此 fileencoding 建议设置为 chinese (chinese 是个别名，在 Unix 里表示 gb2312，在 Windows 里表示cp936，也就是 GBK 的代码页)。