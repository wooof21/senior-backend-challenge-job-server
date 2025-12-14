from flask import Flask, request, jsonify
import random, uuid, time

app = Flask(__name__)

# ---- State (shared across requests) ----
failure_count = 0
fail_until = 0
delay_seconds = 5
always_fail = False

@app.route("/process", methods=["POST"])
def process():
    global failure_count

    # Validate JSON body
    if not request.is_json:
        return jsonify({"error": "Invalid or missing JSON"}), 400

    data = request.get_json()
    # Example: require 'jobId' field in the request
    if "jobId" not in data:
        return jsonify({"error": "Missing 'jobId' field"}), 400

    # Simulate slow processing
    time.sleep(delay_seconds)

     # Always fail mode (for circuit breaker testing)
    if always_fail:
        return jsonify({"error": "Service down"}), 500

    # Fail N times, then succeed (for retry testing)
    if failure_count < fail_until:
        failure_count += 1
        return jsonify({"error": f"Temporary failure {failure_count}"}), 500

    # Return random job result
    return jsonify({
        "jobId": data["jobId"],
        "value": random.randint(1, 1000),
        "status": "DONE"
    })

# ---- Control endpoints ----

@app.route("/control/fail/<int:n>", methods=["POST"])
def set_fail_n_times(n):
    global failure_count, fail_until, always_fail
    failure_count = 0
    fail_until = n
    always_fail = False
    return jsonify({"message": f"Will fail {n} times"})


@app.route("/control/always_fail", methods=["POST"])
def set_always_fail():
    global always_fail
    always_fail = True
    return jsonify({"message": "Always fail enabled"})


@app.route("/control/delay/<int:seconds>", methods=["POST"])
def set_delay(seconds):
    global delay_seconds
    delay_seconds = seconds
    return jsonify({"message": f"Delay set to {seconds}s"})

@app.route("/control/reset", methods=["POST"])
def reset():
    global failure_count, fail_until, always_fail, delay_seconds
    failure_count = 0
    fail_until = 0
    always_fail = False
    delay_seconds = 5
    return jsonify({"message": "State reset"})



if __name__ == "__main__":
    app.run(host="0.0.0.0", port=8081)

