from flask import Flask, request, jsonify
import random, uuid, time

app = Flask(__name__)

@app.route("/process", methods=["POST"])
def process():
    # Validate JSON body
    if not request.is_json:
        return jsonify({"error": "Invalid or missing JSON"}), 400

    data = request.get_json()
    # Example: require 'jobId' field in the request
    if "jobId" not in data:
        return jsonify({"error": "Missing 'jobId' field"}), 400

    # Simulate slow processing
    time.sleep(3)

    # Return random job result
    return jsonify({
        "jobId": data["jobId"],
        "value": random.randint(1, 1000),
        "status": "DONE"
    })

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=8081)

