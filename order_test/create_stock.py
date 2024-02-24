import mysql.connector
from mysql.connector import Error
from datetime import datetime, timedelta

def insert_products():
    try:
        # 데이터베이스 연결 설정
        connection = mysql.connector.connect(
            host='localhost',
            port=3310,
            database='pre_order_stock',
            user='hanghae',
            password='hanghae'
        )
        if connection.is_connected():
            cursor = connection.cursor()

            # 재고 등록
            stock_query = "INSERT INTO stock (item_num, stock) VALUES (%s, %s)"
            cursor.execute(stock_query, (1, 10))

            retrieve_query = "SELECT * FROM stock WHERE item_num = %s"
            cursor.execute(retrieve_query, (1 ,))
            inserted_data = cursor.fetchone()

            print(f"삽입된 데이터: {inserted_data}")

            cursor.execute(stock_query, (2, 10))

            retrieve_query = "SELECT * FROM stock WHERE item_num = %s"
            cursor.execute(retrieve_query, (2 ,))
            inserted_data = cursor.fetchone()
            print(f"삽입된 데이터: {inserted_data}")

            connection.commit()

    except Error as e:
        print("데이터베이스 연결 또는 쿼리 실행 중 오류 발생:", e)
    finally:
        if connection.is_connected():
            cursor.close()
            connection.close()
            print("MySQL 연결이 종료됨.")

if __name__ == "__main__":
    insert_products()
