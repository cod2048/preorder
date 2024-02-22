import mysql.connector
from mysql.connector import Error
import random
import string

def create_users(num_buyers=10000, num_sellers=2):
    """지정된 수의 구매자와 판매자를 생성하여 데이터베이스에 삽입"""
    try:
        connection = mysql.connector.connect(
            host='localhost',
            database='pre_order',
            user='hanghae',
            password='hanghae'
        )

        if connection.is_connected():
            cursor = connection.cursor()
            for _ in range(num_buyers):
                email = ''.join(random.choices(string.ascii_lowercase + string.digits, k=10)) + "@example.com"
                password = ''.join(random.choices(string.ascii_letters + string.digits, k=10))
                name = ''.join(random.choices(string.ascii_letters + string.digits, k=5))
                user_role = "0"
                cursor.execute("INSERT INTO user (email, password, name, role) VALUES (%s, %s, %s, %s)",
                               (email, password, name, user_role))

            for _ in range(num_sellers):
                email = ''.join(random.choices(string.ascii_lowercase + string.digits, k=10)) + "@example.com"
                password = ''.join(random.choices(string.ascii_letters + string.digits, k=10))
                name = ''.join(random.choices(string.ascii_letters + string.digits, k=5))
                user_role = "1"
                cursor.execute("INSERT INTO user (email, password, name, role) VALUES (%s, %s, %s, %s)",
                               (email, password, name, user_role))

            connection.commit()
            print(f"{num_buyers}명의 구매자와 {num_sellers}명의 판매자가 생성되었습니다.")

    except Error as e:
        print("데이터베이스 연결 또는 쿼리 실행 중 오류 발생:", e)
    finally:
        if connection.is_connected():
            cursor.close()
            connection.close()
            print("MySQL 연결이 종료됨.")

if __name__ == "__main__":
    create_users(10000, 2)
