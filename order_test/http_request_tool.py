import requests
import random
from concurrent.futures import ThreadPoolExecutor

def send_http_request(buyerNum):
    order_url = "http://localhost:8080/api/v1/orders"
    payment_url = "http://localhost:8080/api/v1/orders/try-payments"

    cancel_request = random.random() < 0.2

    if cancel_request:
        print(f"Request to {order_url} with buyerNum {buyerNum} canceled")
        return

    try:
        # POST 요청을 보냄
        response = requests.post(order_url, json={"buyerNum": buyerNum, "itemNum": 2, "quantity": 1, "price": 1000})
        print(f"Request to {order_url} with buyerNum {buyerNum} completed with status code {response.status_code}")

        # 응답을 JSON 형식으로 파싱
        data = response.json()

        # "orderNum" 키가 있는지 확인
        if "orderNum" in data.get("data", {}):
            orderId = data["data"]["orderNum"]
            print(f"Received orderId: {orderId}")

            # 결제 요청을 보냄
            payment_response = requests.post(f"{payment_url}/{orderId}")

            if payment_response.status_code == 200:
                payment_data = payment_response.json()
                print(f"Payment for orderId {orderId} completed successfully, {payment_data['data']}")
            else:
                print(f"Payment for orderId {orderId} failed with status code {payment_response.status_code}")
        else:
            print("Response does not contain 'orderNum' key")

    except requests.exceptions.RequestException as e:
        print(f"Error sending request to {order_url}: {e}")

def main():
    # Set the number of concurrent requests (N)
    num_requests = 10000  # buyerNum을 1부터 10000까지 보낼 것이므로 요청 수를 10000으로 설정

    # Create a ThreadPoolExecutor to send concurrent requests
    with ThreadPoolExecutor(max_workers=100) as executor:
        # Use a list comprehension to create a list of tasks
        tasks = [executor.submit(send_http_request, buyerNum) for buyerNum in range(1, num_requests + 1)]

        # Wait for all tasks to complete
        for future in tasks:
            future.result()

if __name__ == "__main__":
    main()
