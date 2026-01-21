const amqp = require("amqplib");

const RABBITMQ_URL = "amqp://user:password@rabbitmq:5672";
const QUEUE = "user.registered";

async function startConsumer() {
  try {
    console.log("Consumer connecting...");
    const conn = await amqp.connect(RABBITMQ_URL);

    conn.on("close", () => {
      console.log("RabbitMQ connection closed. Reconnecting...");
      setTimeout(startConsumer, 3000);
    });

    conn.on("error", (err) => {
      console.error("RabbitMQ error:", err.message);
    });

    const channel = await conn.createChannel();
    await channel.assertQueue(QUEUE, { durable: true });

    console.log("Consumer waiting for messages...");

    channel.consume(QUEUE, (msg) => {
      if (msg) {
        const event = JSON.parse(msg.content.toString());
        console.log("Send welcome email to:", event.email);
        channel.ack(msg);
      }
    });
  } catch {
    console.log("Consumer retry in 3s...");
    setTimeout(startConsumer, 3000);
  }
}

startConsumer();
