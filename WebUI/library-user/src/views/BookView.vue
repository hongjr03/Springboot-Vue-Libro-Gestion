<template>
  <div class="book">
    <el-container>
      <el-main>
        <!--        图书操作栏-->
        <el-row class="book-header">
          <el-col :span="4" class="search-page-pane">
            <el-select
              v-model="pageSize"
              placeholder="数据显示"
              @change="changeSize"
              class="search-size"
            >
              <el-option
                v-for="item in sizeOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              >
              </el-option>
            </el-select>
          </el-col>
          <el-col :span="16" class="search-input-pane">
            <el-row>
              <el-col :span="4">
                <el-select
                  v-model="searchModel"
                  placeholder="搜索类型"
                  @change="changeSearch"
                  class="search-size"
                >
                  <el-option
                    v-for="item in searchOptions"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  >
                  </el-option>
                </el-select>
              </el-col>
              <el-col :span="14">
                <el-input
                  placeholder="请输入搜索内容"
                  v-model="searchInput"
                  :suffix-icon="Search"
                  class="search-input"
                >
                </el-input>
              </el-col>
              <el-col :span="6">
                <el-button
                  type="primary"
                  class="search-button"
                  @click="searchButton"
                >
                  搜索
                </el-button>
              </el-col>
            </el-row>
          </el-col>
          <el-col :span="4" class="search-page-pane">
            <el-button
              type="primary"
              class="borrow-button"
              @click="borrowSelectedBook"
            >
              借阅选中图书
            </el-button>
          </el-col>
        </el-row>
        <!--        图书表格栏-->
        <el-row class="book-table">
          <el-col>
            <el-table
              :data="books"
              height="100%"
              empty-text="没有数据"
              @selection-change="updateSelection"
            >
              <!-- 是否选中 -->
              <el-table-column type="selection" width="50" />
              <el-table-column fixed prop="id" label="Id" width="50" />
              <el-table-column prop="groups" label="组名" />
              <el-table-column prop="name" label="书名" />
              <el-table-column prop="author" label="作者" />
              <el-table-column prop="press" label="出版社" />
              <el-table-column prop="price" label="价格(人民币)" />
              <el-table-column prop="quantity" label="数量(本)" />
              <el-table-column prop="isbn" label="ISBN号码" />
              <!-- 借阅按钮 -->
              <el-table-column fixed="right" label="操作">
                <template #default="{ row }">
                  <el-button @click="borrowBook(row, true)" type="text"
                    >借阅</el-button
                  >
                </template>
              </el-table-column>
            </el-table>
            <el-pagination
              background
              v-model:current-page="pageNum"
              v-model:page-size="pageSize"
              layout="prev, pager, next"
              :total="pageTotal"
              @current-change="page"
            >
            </el-pagination>
          </el-col>
        </el-row>
      </el-main>
    </el-container>
  </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from "vue";
import { Search } from "@element-plus/icons-vue";
import axios from "axios";
import jsCookie from "js-cookie";
import { ElMessageBox } from "element-plus";

// 获取图书数据
let books = ref();
const getBook = () => {
  axios
    .get("http://localhost:8888/book/" + pageNum.value + "/" + pageSize.value)
    .then((resp) => {
      books.value = resp.data.content;
      books.value.forEach((book: any) => {
        book["selected"] = false;
      });
      pageTotal.value = resp.data.totalElements;
      // console.log(resp.data.content);
    });
};

// 显示数据数量选项
let pageNum = ref(1);
let pageSize = ref(10);
let pageTotal = ref(0);
const page = (val: number) => {
  pageNum.value = val;
  if (searchInput.value == undefined) {
    getBook();
  } else {
    searchBook();
  }
};

// 数据显示框
let sizeOptions = [
  {
    value: 10,
    label: "10条数据/页",
  },
  {
    value: 50,
    label: "50条数据/页",
  },
  {
    value: 100,
    label: "100条数据/页",
  },
];

// 修改显示数据量
const changeSize = (value: number) => {
  pageSize.value = value;
  if (searchInput.value == undefined) {
    getBook();
  } else {
    searchBook();
  }
};

// 搜索框选项
let searchModel = ref("name");
let searchOptions = [
  {
    value: "name",
    label: "图书名称",
  },
  {
    value: "author",
    label: "作者名称",
  },
  {
    value: "press",
    label: "出版社名称",
  },
  {
    value: "isbn",
    label: "ISBN号码",
  },
];
const changeSearch = (value: string) => {
  searchModel.value = value;
};

// 搜索框数据
const searchInput = ref();
// 搜索框按钮
const searchButton = () => {
  pageNum.value = 1;
  searchBook();
};
// 搜索图书
const searchBook = () => {
  if (searchInput.value != "") {
    axios
      .get(
        "http://localhost:8888/book/search/" +
          searchModel.value +
          "/" +
          searchInput.value +
          "/" +
          pageNum.value +
          "/" +
          pageSize.value
      )
      .then((resp) => {
        books.value = resp.data.content;
        pageTotal.value = resp.data.totalElements;
      });
  } else {
    getBook();
  }
};

// 用户名
let username = jsCookie.get("username");
const userInfo = reactive({
  name: jsCookie.get("username"),
  idCard: jsCookie.get("idCard"),
  phone: jsCookie.get("phone"),
});

const updateSelection = (selectedRows: string | any[]) => {
  books.value.forEach((book: any) => {
    book.selected = selectedRows.includes(book);
  });
};

const borrowBook = (row: any, messageEnabled: boolean) => {
  let statusCode = 0;
  axios
    .post("http://localhost:8888/borrow/" + row["isbn"], userInfo)
    .then((resp) => {
      statusCode = resp.data.statusCode;
      if (messageEnabled) {
        // 借阅成功
        if (statusCode == 1) {
          ElMessageBox.alert("借阅成功", "信息", {
            confirmButtonText: "确认",
          });
        }
        // 借阅失败
        if (statusCode == 0) {
          ElMessageBox.alert("借阅失败，请重新借阅", "信息", {
            confirmButtonText: "确认",
          });
        }
        // 借阅失败，库存不足
        if (statusCode == 2) {
          ElMessageBox.alert("借阅失败，库存不足", "信息", {
            confirmButtonText: "确认",
          });
        }
        // 借阅失败，卡号不存在
        if (statusCode == 3) {
          ElMessageBox.alert("借阅失败，卡号不存在", "信息", {
            confirmButtonText: "确认",
          });
        }
        // 借阅失败，用户可借阅数量已达上限
        if (statusCode == 4) {
          ElMessageBox.alert("借阅失败，用户可借阅数量已达上限", "信息", {
            confirmButtonText: "确认",
          });
        }
      }
    });
  return statusCode;
};

const borrowSelectedBook = () => {
  const selectedBooks = books.value.filter((book: any) => {
    return book["selected"] == true;
  });
  console.log(selectedBooks);
  if (selectedBooks.length == 0) {
    ElMessageBox.alert("请选择要借阅的图书", "信息", {
      confirmButtonText: "确认",
    });
  } else {
    (async () => {
      for (let i = 0; i < selectedBooks.length; i++) {
        borrowBook(selectedBooks[i], false);
      }

      await ElMessageBox.alert(
        "成功执行借阅操作，请验证是否全部借阅成功",
        "信息",
        {
          confirmButtonText: "确认",
          callback: () => {
            getBook();
          },
        }
      );
    })();
  }
  getBook(); // 重新获取图书信息
};

// 初始化
const init = () => {
  // 获取图书
  axios.get("http://localhost:8888/user/get/" + username).then((resp) => {
    userInfo.name = resp.data.name;
    userInfo.idCard = resp.data.idCard;
    userInfo.phone = resp.data.phone;
    console.log(resp.data);
    getBook();
  });
};
init();
</script>

<style lang="scss">
@import "../assets/css/book";
</style>
