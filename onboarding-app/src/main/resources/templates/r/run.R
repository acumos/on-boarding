p <- parallel::mcparallel(acumos:::run(runtime=list(input_port=8100)))

acumos:::run(metadata="generator.json", payload="generator.bin", proto="generator.proto",
              runtime=list(output_url="http://localhost:8100/predict"))

warnings()

## shut down the RF component
parallel:::mckill(p)
parallel:::mccollect(p)
parallel:::mccollect(p)

