from timeit import default_timer
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
    start = default_timer()
    yield
    end = default_timer()
    duration_seconds = (end - start)
    if duration_seconds * 1000 < 5:
        print(f"Elapsed time: {duration_seconds * 1e6:.0f} microseconds")
    else:
        print(f"Elapsed time: {duration_seconds * 1e3:.0f} ms")