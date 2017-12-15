FROM jfloff/alpine-python:3.5-slim

# Set the working directory to /app
WORKDIR /app

# Copy the current directory contents into the container at /app
ADD . /app

#--proxy http://pb00365848:!PB123456a%40@10.254.40.121:8080
RUN pip install  pytest-runner


RUN pip install -r requirements.txt --extra-index-url {1} --trusted-host {2}

#Make port 80 available to the world outside this container
EXPOSE 3330

# Run runner.py when the container launches
CMD ["python", "runner.py"]