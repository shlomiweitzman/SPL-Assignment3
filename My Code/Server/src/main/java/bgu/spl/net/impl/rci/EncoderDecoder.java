package bgu.spl.net.impl.rci;

import bgu.spl.net.Pair;
import bgu.spl.net.api.MessageEncoderDecoder;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Vector;

public class EncoderDecoder<T> implements MessageEncoderDecoder<T> {
    private ByteBuffer lengthBuffer = ByteBuffer.allocate(1);
    private byte[] opcode = null;
    private byte[] objectBytes = null;
    private boolean checkedOpcode = false;
    private String[] output;
    private Vector<Byte> input = new Vector<>();
    private int opC;
    private int objectBytesIndex = 0;
    private int i = 0;
    private boolean finishedRead = false;

    @Override
    public T decodeNextByte(byte nextByte) {
        if (opcode == null || objectBytes == null) {
            lengthBuffer.put(nextByte);
            if (!lengthBuffer.hasRemaining()) {
                lengthBuffer.flip();
            }
            if (opcode == null && !checkedOpcode) {
                opcode = new byte[2];
            }
        }
        if (!checkedOpcode && opcode != null) {
            opcode[i] = nextByte;
            i++;
            if (i == opcode.length) {
                short result = (short) ((opcode[0] & 0xff) << 8);
                result += (short) (opcode[1] & 0xff);
                opC = ((Short) result).intValue();
                checkedOpcode = true;
                objectBytesIndex = 0;
                i = 0;
            }
        } else if (checkedOpcode) {
            if (opC == 1 || opC == 6 || opC == 2 && !finishedRead) {
                if (output == null && i == 0) {
                    output = new String[2];
                    objectBytes = null;
                    lengthBuffer.clear();
                    objectBytes = new byte[1];
                    i++;
                }
                if (i == 1 || i == 2) {
                    objectBytes[0] = nextByte;
                    lengthBuffer.clear();
                }

                if (i != 0 && objectBytes[0] != 0) {
                    input.add(objectBytes[0]);


                } else if (i == 1 && !finishedRead) {
                    byte[] bytes = vectorToarray(input);
                    output[0] = new String(bytes, StandardCharsets.UTF_8);
                    input.clear();
                    i++;

                } else if (i == 2 && !finishedRead) {
                    byte[] bytes = vectorToarray(input);
                    output[1] = new String(bytes, StandardCharsets.UTF_8);
                    finishedRead = true;
                }
            }
            //---------------------------------1/2/6------------------------------------
            else if (opC == 4 && !finishedRead) {
                if (output == null && i == 0) {
                    output = new String[2];
                    lengthBuffer.clear();
                    objectBytes = null;
                    objectBytes = new byte[1];
                    objectBytesIndex = 0;
                    i++;
                }
                objectBytes[objectBytesIndex++] = nextByte;
                lengthBuffer.clear();
                if (i == 1) {
                    if (objectBytes[0] == 1)
                        output[0] = "unfollow";
                    else
                        output[0] = "follow";
                    i++;
                    lengthBuffer.clear();
                    objectBytes = null;
                    objectBytesIndex = 0;
                    objectBytes = new byte[2];
                }
                if (objectBytesIndex == objectBytes.length && i == 2) {
                    short result2 = bytesToShort(objectBytes);
                    int numOfUsers = result2;
                    String f = output[0];
                    output = null;
                    output = new String[numOfUsers + 2];
                    output[0] = f;
                    output[1] = String.valueOf(numOfUsers);
                    objectBytes = null;
                    objectBytesIndex = 0;
                    objectBytes = new byte[1];
                    i++;
                }
                if (objectBytes.length == 1 &&
                        objectBytesIndex == objectBytes.length && objectBytes[0] != 0 && i <= output.length) {//0 separate the users names
                    objectBytesIndex = 0;
                    input.add(objectBytes[0]);
                    objectBytes = null;
                    objectBytes = new byte[1];
                }
                if (objectBytes.length == 1 && objectBytesIndex == objectBytes.length &&
                        i <= output.length) {
                    byte[] bytes = vectorToarray(input);
                    output[i - 1] = new String(bytes, StandardCharsets.UTF_8);
                    input.clear();
                    objectBytesIndex = 0;
                    i++;
                }
                if (i > output.length) {
                    finishedRead = true;
                }
            }
            //---------------------------------4------------------------------------
            else if (opC == 5 || opC == 8 && !finishedRead) {
                if (output == null && i == 0) {
                    output = new String[1];
                    lengthBuffer.clear();
                    objectBytes = null;
                    objectBytes = new byte[1];
                    i++;
                }
                if (i == 1) {
                    objectBytes[0] = nextByte;
                    lengthBuffer.clear();
                }
                if (objectBytes[0] != 0) {
                    input.add(objectBytes[0]);
                    lengthBuffer.clear();
                    objectBytes = null;
                    objectBytes = new byte[1];
                } else {
                    byte[] bytes = vectorToarray(input);
                    output[0] = new String(bytes, StandardCharsets.UTF_8);
                    finishedRead = true;
                    input.clear();
                }
            }
            //---------------------------------5/8-----------------------------------
        }
        if (checkedOpcode) {
            if (opC == 3 || opC == 7 & !finishedRead) {
                output = new String[1];
                finishedRead = true;
            }
            //---------------------------------3/7------------------------------------
        }
        if (checkedOpcode && finishedRead) {
            if (opC != 5) {
                Pair<Integer, String[]> pair = new Pair<>(opC, output);
                lengthBuffer.clear();
                objectBytes = null;
                opcode = null;
                checkedOpcode = false;
                finishedRead = false;
                i = 0;
                opC = 0;
                objectBytesIndex = 0;
                input.clear();
                output = null;
                return (T) pair;
            } else {
                Post post;
                post = new Post(output[0]);
                Pair<Integer, Post> pair = new Pair<>(opC, post);
                objectBytes = null;
                opcode = null;
                objectBytesIndex = 0;
                finishedRead = false;
                checkedOpcode = false;
                i = 0;
                opC = 0;
                lengthBuffer.clear();
                input.clear();
                output = null;
                return (T) pair;
            }
        }
        return null;
    }

    public byte[] encode(T message) {
        if (message instanceof ACK) {
            int opcode = ((ACK) message).getMessageOpcode();
            byte[] output = new byte[4];
            output[0] = (byte) (((short) 10 >> 8) & 0xFF);
            output[1] = (byte) ((short) 10 & 0xFF);
            //------ACK-opcode
            output[2] = (byte) (((short) opcode >> 8) & 0xFF);
            output[3] = (byte) ((short) opcode & 0xFF);
            //----msg-opcode
            if (opcode == 8) {
                byte[] newOutput = new byte[10];
                for (int i = 0; i < 4; i++)
                    newOutput[i] = output[i];
                newOutput[4] = (byte) (((short) ((ACK8) message).getNumPosts() >> 8) & 0xFF);
                newOutput[5] = (byte) ((short) ((ACK8) message).getNumPosts() & 0xFF);
                newOutput[6] = (byte) ((short) (((ACK8) message).getNumFollowers() >> 8) & 0xFF);
                newOutput[7] = (byte) ((short) ((ACK8) message).getNumFollowers() & 0xFF);
                newOutput[8] = (byte) ((short) (((ACK8) message).getNumFollowing() >> 8) & 0xFF);
                newOutput[9] = (byte) ((short) ((ACK8) message).getNumFollowing() & 0xFF);
                return newOutput;
            }

            if (opcode == 4 || opcode == 7) {
                byte[] newOutput1 = new byte[0];
                try {
                    newOutput1 = ((ACK4O7) message).getUserListBytes();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                int size = newOutput1.length + 6;
                byte[] newOutput = new byte[size];
                for (int i = 0; i < 4; i++)
                    newOutput[i] = output[i];
                newOutput[4] = (byte) ((((short) ((ACK4O7) message).getNumOfUsers()) >> 8) & 0xFF);
                newOutput[5] = (byte) ((short) ((ACK4O7) message).getNumOfUsers() & 0xFF);
                for (int i = 0; i < newOutput1.length; i++) {
                    newOutput[i + 6] = newOutput1[i];
                }
                return newOutput;
            }
            return output;
        }
        if (message instanceof Error) {
            int opcode = ((Error) message).getMessageOpcode();
            byte[] output = new byte[4];
            output[0] = (byte) ((short) 11 >> 8 & 0xFF);
            output[1] = (byte) ((short) 11 & 0xFF);
            //------error-opcode
            output[2] = (byte) ((short) opcode >> 8 & 0xFF);
            output[3] = (byte) ((short) opcode & 0xFF);
            //----msg-opcode
            return output;
        }
        if (message instanceof Notifications) {
            byte[] pstingUser = new byte[0];
            try {
                pstingUser = ((Notifications) message).getPostingUser().getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            byte[] content = new byte[0];
            try {
                content = ((Notifications) message).getContent().getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            byte[] output = new byte[pstingUser.length + content.length + 5];
            output[0] = (byte) ((short) 9 >> 8 & 0xFF);
            output[1] = (byte) ((short) 9 & 0xFF);
            if (((Notifications) message).getMsg() == "post") {
                byte[] tmp = new byte[1];
                tmp[0] = 1;
                output[2] = tmp[0];
            } else {
                byte[] tmp = new byte[1];
                tmp[0] = 0;
                output[2] = tmp[0];
            }
            for (int i = 0; i < pstingUser.length; i++) {
                output[i + 3] = pstingUser[i];
            }
            output[3 + pstingUser.length] = 0;

            for (int i = 0; i < content.length; i++) {
                output[i + 4 + pstingUser.length] = content[i];
            }
            output[output.length - 1] = 0;
            return output;
        }
        return new byte[0];
    }

    public byte[] vectorToarray(Vector<Byte> v) {
        byte[] bytes = new byte[v.size()];
        for (int i = 0; i < v.size(); i++) {
            bytes[i] = v.get(i);
        }
        return bytes;
    }

    public short bytesToShort(byte[] byteArr) {
        short result = (short) ((byteArr[0] & 0xff) << 8);
        result += (short) (byteArr[1] & 0xff);
        return result;
    }
}

