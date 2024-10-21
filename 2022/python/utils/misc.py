import time
from contextlib import contextmanager

def partition(chunk_size, step_size, iterable):
    chunk_start = 0
    chunk = iterable[chunk_start:chunk_start + chunk_size]
    chunks = []
    while len(chunk) == chunk_size:
        chunks.append(chunk)
        chunk_start += step_size
        chunk = iterable[chunk_start:chunk_start + chunk_size]
    return chunks

@contextmanager
def timer():
    start = time.time()
    yield
    end = time.time()
    print(f"Elapsed time: {(end - start) * 1000:.2f} ms")