# Generated by the protocol buffer compiler.  DO NOT EDIT!
# source: model_pb2.proto

import sys
_b=sys.version_info[0]<3 and (lambda x:x) or (lambda x:x.encode('latin1'))
from google.protobuf import descriptor as _descriptor
from google.protobuf import message as _message
from google.protobuf import reflection as _reflection
from google.protobuf import symbol_database as _symbol_database
from google.protobuf import descriptor_pb2
# @@protoc_insertion_point(imports)

_sym_db = _symbol_database.Default()




DESCRIPTOR = _descriptor.FileDescriptor(
  name='model_pb2.proto',
  package='',
  syntax='proto3',
  serialized_pb=_b('\n\x0fmodel_pb2.proto\"g\n\tDataFrame\x12\x0b\n\x03\x64\x61y\x18\x01 \x03(\x03\x12\x0f\n\x07weekday\x18\x02 \x03(\x03\x12\x0c\n\x04hour\x18\x03 \x03(\x03\x12\x0e\n\x06minute\x18\x04 \x03(\x03\x12\x0f\n\x07hist_1D\x18\x05 \x03(\x01\x12\r\n\x05VM_ID\x18\x06 \x03(\x03\"!\n\nPrediction\x12\x13\n\x0bpredictions\x18\x01 \x03(\x01\x32-\n\x05Model\x12$\n\ttransform\x12\n.DataFrame\x1a\x0b.Predictionb\x06proto3')
)




_DATAFRAME = _descriptor.Descriptor(
  name='DataFrame',
  full_name='DataFrame',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    _descriptor.FieldDescriptor(
      name='day', full_name='DataFrame.day', index=0,
      number=1, type=3, cpp_type=2, label=3,
      has_default_value=False, default_value=[],
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      options=None),
    _descriptor.FieldDescriptor(
      name='weekday', full_name='DataFrame.weekday', index=1,
      number=2, type=3, cpp_type=2, label=3,
      has_default_value=False, default_value=[],
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      options=None),
    _descriptor.FieldDescriptor(
      name='hour', full_name='DataFrame.hour', index=2,
      number=3, type=3, cpp_type=2, label=3,
      has_default_value=False, default_value=[],
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      options=None),
    _descriptor.FieldDescriptor(
      name='minute', full_name='DataFrame.minute', index=3,
      number=4, type=3, cpp_type=2, label=3,
      has_default_value=False, default_value=[],
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      options=None),
    _descriptor.FieldDescriptor(
      name='hist_1D', full_name='DataFrame.hist_1D', index=4,
      number=5, type=1, cpp_type=5, label=3,
      has_default_value=False, default_value=[],
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      options=None),
    _descriptor.FieldDescriptor(
      name='VM_ID', full_name='DataFrame.VM_ID', index=5,
      number=6, type=3, cpp_type=2, label=3,
      has_default_value=False, default_value=[],
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      options=None),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=19,
  serialized_end=122,
)


_PREDICTION = _descriptor.Descriptor(
  name='Prediction',
  full_name='Prediction',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    _descriptor.FieldDescriptor(
      name='predictions', full_name='Prediction.predictions', index=0,
      number=1, type=1, cpp_type=5, label=3,
      has_default_value=False, default_value=[],
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      options=None),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=124,
  serialized_end=157,
)

DESCRIPTOR.message_types_by_name['DataFrame'] = _DATAFRAME
DESCRIPTOR.message_types_by_name['Prediction'] = _PREDICTION
_sym_db.RegisterFileDescriptor(DESCRIPTOR)

DataFrame = _reflection.GeneratedProtocolMessageType('DataFrame', (_message.Message,), dict(
  DESCRIPTOR = _DATAFRAME,
  __module__ = 'model_pb2_pb2'
  # @@protoc_insertion_point(class_scope:DataFrame)
  ))
_sym_db.RegisterMessage(DataFrame)

Prediction = _reflection.GeneratedProtocolMessageType('Prediction', (_message.Message,), dict(
  DESCRIPTOR = _PREDICTION,
  __module__ = 'model_pb2_pb2'
  # @@protoc_insertion_point(class_scope:Prediction)
  ))
_sym_db.RegisterMessage(Prediction)


# @@protoc_insertion_point(module_scope)
