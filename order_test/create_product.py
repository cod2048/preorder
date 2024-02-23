import mysql.connector
from mysql.connector import Error
from datetime import datetime, timedelta

def insert_products():
    try:
        # 데이터베이스 연결 설정
        connection = mysql.connector.connect(
            host='localhost',
            port=3307,
            database='pre_order_item',
            user='hanghae',
            password='hanghae'
        )
        if connection.is_connected():
            cursor = connection.cursor()

            # 일반 상품 등록
            normal_product_query = """
            INSERT INTO item (seller_num, title, description, price, available_at, end_at) 
            VALUES (%s, %s, %s, %s, %s, %s)
            """
            normal_product_values = (10001, '일반 상품', '일반 상품 설명', 10000, None, None)
            cursor.execute(normal_product_query, normal_product_values)
            normal_product_id = cursor.lastrowid

            # 예약 상품 등록
            pre_order_product_query = """
            INSERT INTO item (seller_num, title, description, price, available_at, end_at) 
            VALUES (%s, %s, %s, %s, %s, %s)
            """
            available_at = datetime(2024, 2, 22, 7, 0, 0)
            # end_at = available_at + timedelta(hours=2)
            end_at = datetime(2025, 2, 22, 7, 0, 0)
            pre_order_product_values = (10002, '예약 상품', '예약 상품 설명', 15000, available_at, end_at)
            cursor.execute(pre_order_product_query, pre_order_product_values)
            pre_order_product_id = cursor.lastrowid

            # 재고 등록
            stock_query = "INSERT INTO stock (item_num, stock) VALUES (%s, %s)"
            cursor.execute(stock_query, (normal_product_id, 10))
            cursor.execute(stock_query, (pre_order_product_id, 10))

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
