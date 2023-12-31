<template>
  <div class="borrow">
    <el-container>
      <el-main>
        <!--        借阅图书面板-->
        <el-row class="borrow-pane">
          <el-col>
            <el-col><span class="title">借阅图书</span></el-col>
            <!--            图书搜索框-->
            <el-col class="search-book-pane"
              ><el-input
                class="search-book-input"
                placeholder="请输入要搜索的ISBN号码"
                v-model="searchBookInput"
                maxlength="13"
              ></el-input
              ><el-button
                class="search-book-button"
                type="primary"
                @click="searchBook"
                >搜索</el-button
              ></el-col
            >
          </el-col>
          <el-col>
            <el-row>
              <!--              图书信息面板-->
              <el-col :span="12" class="book-info-pane">
                <span class="title">图书信息</span>
                <span class="sub-title">图书名称：{{ book.name }}</span>
                <span class="sub-title">作者名称：{{ book.author }}</span>
                <span class="sub-title">出版社名称：{{ book.press }}</span>
                <span class="sub-title">ISBN号码：{{ book.isbn }}</span>
                <span class="sub-title">库存（本）：{{ book.quantity }}</span>
              </el-col>
              <!--              用户表单-->
              <el-col :span="12" class="user-form-pane">
                <el-form v-model="userInfo" ref="userInfoRef">
                  <span class="title">用户信息</span>
                  <span class="sub-title">借阅人名字：{{ userInfo.name }}</span>
                  <span class="sub-title"
                    >借阅人卡号：{{ userInfo.idCard }}</span
                  >
                  <span class="sub-title"
                    >借阅人手机号：{{ userInfo.phone }}</span
                  >
                  <el-button type="primary" @click="borrowBook"
                    >借阅图书</el-button
                  >
                </el-form>
              </el-col>
            </el-row>
          </el-col>
        </el-row>
      </el-main>
    </el-container>
  </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from "vue";
import type { FormInstance } from "element-plus";
import axios from "axios";
import { ElMessageBox } from "element-plus";
import jsCookie from "js-cookie";

// 搜索图书输入框
const searchBookInput = ref();
const searchBook = () => {
  axios
    .get("http://localhost:8888/book/find/isbn/" + searchBookInput.value)
    .then((resp) => {
      book.value = resp.data;
    });
};

// 图书信息
let book = ref([
  {
    name: "",
    author: "",
    press: "",
    isbn: "",
    quantity: "",
  },
]);

// 用户信息
const userInfoRef = ref<FormInstance>();
const userInfo = reactive({
  name: jsCookie.get("username"),
  idCard: jsCookie.get("idCard"),
  phone: jsCookie.get("phone"),
});

// 借阅图书按钮
const borrowBook = () => {
  axios
    .post("http://localhost:8888/borrow/" + searchBookInput.value, userInfo)
    .then((resp) => {
      const statusCode = resp.data.statusCode;

      // 借阅失败
      if (statusCode == 0) {
        ElMessageBox.alert("借阅失败，请重新借阅", "信息", {
          confirmButtonText: "确认",
        });
      }

      // 借阅成功
      if (statusCode == 1) {
        ElMessageBox.alert("借阅成功", "信息", {
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
    });
};

const init = () => {
  // 获取图书
  let username = jsCookie.get("username");
  axios.get("http://localhost:8888/user/get/" + username).then((resp) => {
    userInfo.name = resp.data.name;
    userInfo.idCard = resp.data.idCard;
    userInfo.phone = resp.data.phone;
    console.log(resp.data);
  });
};
init();
</script>

<style lang="scss">
@import "../assets/css/borrow";
</style>
