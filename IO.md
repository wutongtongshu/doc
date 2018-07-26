# 1 NIO

## 1.1 ByteBuffer

flip()：limit=position, position=0, capacity不变。读写反转的意思

clear()：limit=capacity, position=0, capacity不变。清空，达到最大程度

rewind()：position=0，limit和capacity不变，重新执行一遍

compact()：将 [position, limit] 之间的数据移动到 buffer 最前端，(limit, capacity]之间不管有没有数据统统删除。