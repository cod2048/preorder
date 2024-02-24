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
            normal_product_id = cursor.lastrowid

            cursor.execute(stock_query, (2, 10))
            pre_order_product_id = cursor.lastrowid

            connection.commit()

            print(f"일반 상품과 예약 상품이 성공적으로 등록되었습니다. 상품 번호: {normal_product_id}, {pre_order_product_id}")

    except Error as e:
        print("데이터베이스 연결 또는 쿼리 실행 중 오류 발생:", e)
    finally:
        if connection.is_connected():
            cursor.close()
            connection.close()
            print("MySQL 연결이 종료됨.")

if __name__ == "__main__":
    insert_products()
